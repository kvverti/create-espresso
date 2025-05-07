package systems.thedawn.espresso.drink;

import java.util.List;
import java.util.Optional;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoRegistries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BuiltinDrinkModifiers {
    public static final ResourceKey<DrinkModifier> ICE = register("ice");
    public static final ResourceKey<DrinkModifier> BUBBLES = register("bubbles");
    public static final ResourceKey<DrinkModifier> MILK = register("milk");
    public static final ResourceKey<DrinkModifier> CHOCOLATE = register("chocolate");

    private static ResourceKey<DrinkModifier> register(String name) {
        return ResourceKey.create(EspressoRegistries.DRINK_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, name));
    }

    public static void bootstrapModifiers(BootstrapContext<DrinkModifier> ctx) {
        ctx.register(ICE, new DrinkModifier(Optional.empty(), List.of()));
        ctx.register(BUBBLES, new DrinkModifier(Optional.empty(), List.of()));
        ctx.register(MILK, new DrinkModifier(Optional.empty(), List.of()));
        ctx.register(CHOCOLATE, new DrinkModifier(Optional.empty(), List.of()));
    }
}
