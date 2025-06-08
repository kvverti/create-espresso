package systems.thedawn.espresso.drink;

import java.util.List;
import java.util.Optional;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoRegistries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;

public class BuiltinDrinkModifiers {
    public static final ResourceKey<DrinkModifier> ICE = register("ice");
    public static final ResourceKey<DrinkModifier> BUBBLES = register("bubbles");
    public static final ResourceKey<DrinkModifier> MILK = register("milk");
    public static final ResourceKey<DrinkModifier> CHOCOLATE = register("chocolate");

    private static ResourceKey<DrinkModifier> register(String name) {
        return ResourceKey.create(EspressoRegistries.DRINK_MODIFIERS, Espresso.modLoc(name));
    }

    public static void bootstrapModifiers(BootstrapContext<DrinkModifier> ctx) {
        ctx.register(ICE, new DrinkModifier(
            Optional.of(new DrinkModifier.BaseTransform.Lengthen(0.6f)),
            List.of(DrinkUtil.effectInstance(MobEffects.FIRE_RESISTANCE, DrinkUtil.seconds(15)))));
        ctx.register(BUBBLES, new DrinkModifier(
            Optional.of(new DrinkModifier.BaseTransform.Strengthen(1)),
            List.of()));
        ctx.register(MILK, new DrinkModifier(
            Optional.of(new DrinkModifier.BaseTransform.Lengthen(1.1f)),
            List.of()));
        ctx.register(CHOCOLATE, new DrinkModifier(Optional.empty(), List.of()));
    }
}
