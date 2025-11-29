package systems.thedawn.espresso.drink;

import java.util.List;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoDrinkEffectTemplates;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.effect.DrinkEffect;
import systems.thedawn.espresso.drink.effect.MobEffectTemplate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
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
        ctx.register(ICE, new DrinkModifier(0, 0.6d, List.of(
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.FIRE_RESISTANCE, DrinkUtil.seconds(15), 0))
        )));
        ctx.register(BUBBLES, new DrinkModifier(1, 1d, List.of()));
        ctx.register(MILK, new DrinkModifier(0, 1.1d, List.of()));
        ctx.register(CHOCOLATE, new DrinkModifier(0, 1d, List.of()));
    }
}
