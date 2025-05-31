package systems.thedawn.espresso.jei;

import java.util.Objects;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.*;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentHolder;
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
        registration.registerSubtypeInterpreter(EspressoItems.FILLED_COFFEE_MUG.value(), drinkComponentInterpreter);

        class DrinkBaseComponentInterpreter<T extends DataComponentHolder> implements ISubtypeInterpreter<T> {
            @Override
            public @Nullable Object getSubtypeData(T ingredient, UidContext context) {
                var drinkComponent = ingredient.get(EspressoDataComponentTypes.DRINK_BASE);
                if(drinkComponent != null) {
                    return drinkComponent.getRegisteredName();
                }
                return null;
            }

            @Override
            public String getLegacyStringSubtypeInfo(T ingredient, UidContext context) {
                return "";
            }
        }
        registration.registerSubtypeInterpreter(EspressoItems.DRINK_BOTTLE.value(), new DrinkBaseComponentInterpreter<>());
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, EspressoFluids.SOURCE_DRINK.value(), new DrinkBaseComponentInterpreter<>());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ModificationRecipeCategory(registration.getJeiHelpers()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var recipes = Objects.requireNonNull(Minecraft.getInstance().getConnection())
            .getRecipeManager()
            .getAllRecipesFor(EspressoRecipeTypes.DRINK_MODIFY.value());
        registration.addRecipes(ModificationRecipeCategory.JEI_TYPE.get(), recipes);
    }
}
