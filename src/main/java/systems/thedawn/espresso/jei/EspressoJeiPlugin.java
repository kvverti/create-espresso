package systems.thedawn.espresso.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.EspressoFluids;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class EspressoJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        var drinkComponentInterpreter = new ISubtypeInterpreter<ItemStack>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                var drinkComponent = ingredient.get(EspressoDataComponentTypes.DRINK);
                if(drinkComponent != null) {
                    return drinkComponent.base().getRegisteredName();
                }
                return null;
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return "";
            }
        };
        registration.registerSubtypeInterpreter(EspressoItems.DRINK_MUG.value(), drinkComponentInterpreter);

        // drink bottle
        registration.registerSubtypeInterpreter(EspressoItems.DRINK_BOTTLE.value(), new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                var drinkComponent = ingredient.get(EspressoDataComponentTypes.DRINK_BASE);
                if(drinkComponent != null) {
                    return drinkComponent.getRegisteredName();
                }
                return null;
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return "";
            }
        });

        // drink fluid
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, EspressoFluids.SOURCE_DRINK.value(), new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(FluidStack ingredient, UidContext context) {
                var drinkComponent = ingredient.get(EspressoDataComponentTypes.DRINK_BASE);
                if(drinkComponent != null) {
                    return drinkComponent.getRegisteredName();
                }
                return null;
            }

            @Override
            public String getLegacyStringSubtypeInfo(FluidStack ingredient, UidContext context) {
                return "";
            }
        });
    }
}
