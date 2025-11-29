package systems.thedawn.espresso.drink;

import java.util.List;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoDrinkEffectTemplates;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.effect.DrinkEffect;
import systems.thedawn.espresso.drink.effect.MobEffectTemplate;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import static systems.thedawn.espresso.drink.DrinkUtil.minutes;
import static systems.thedawn.espresso.drink.DrinkUtil.seconds;

public final class BuiltinEspressoDrinks {
    public static final ResourceKey<Drink> EMPTY = register("empty");

    public static final ResourceKey<Drink> DIRTY_COLD_BREW = register("dirty_cold_brew");
    public static final ResourceKey<Drink> COLD_BREW = register("cold_brew");
    public static final ResourceKey<Drink> POUR_OVER = register("pour_over");
    public static final ResourceKey<Drink> ESPRESSO = register("espresso");
    
    public static final ResourceKey<Drink> COFFEE_TEA = register("coffee_tea");
    public static final ResourceKey<Drink> GREEN_TEA = register("green_tea");
    public static final ResourceKey<Drink> BLACK_TEA = register("black_tea");
    public static final ResourceKey<Drink> HERBAL_TEA = register("herbal_tea");

    public static final ResourceKey<Drink> APPLE_JUICE = register("apple_juice");
    public static final ResourceKey<Drink> FRUIT_PUNCH = register("fruit_punch");

    private static ResourceKey<Drink> register(String name) {
        return ResourceKey.create(EspressoRegistries.DRINKS, Espresso.modLoc(name));
    }

    public static void bootstrapDrinks(BootstrapContext<Drink> ctx) {
        ctx.register(EMPTY, Drink.EMPTY);
        ctx.register(DIRTY_COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            mobEffect(MobEffects.POISON, seconds(5), 0),
            mobEffect(MobEffects.CONFUSION, seconds(5), 0)
        )));
        ctx.register(COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            mobEffect(MobEffects.MOVEMENT_SPEED, minutes(1), 0)
        )));
        ctx.register(POUR_OVER, new Drink(Drink.Type.COFFEE, List.of(
            mobEffect(MobEffects.MOVEMENT_SPEED, minutes(1) + seconds(15), 0),
            mobEffect(MobEffects.NIGHT_VISION, minutes(1) + seconds(15), 0)
        )));
        ctx.register(ESPRESSO, new Drink(Drink.Type.COFFEE, List.of(
            mobEffect(MobEffects.MOVEMENT_SPEED, minutes(2) + seconds(30), 0),
            mobEffect(MobEffects.REGENERATION, minutes(2) + seconds(30), 0)
        )));

        ctx.register(COFFEE_TEA, new Drink(Drink.Type.TEA, List.of()));
        ctx.register(GREEN_TEA, new Drink(Drink.Type.TEA, List.of()));
        ctx.register(BLACK_TEA, new Drink(Drink.Type.TEA, List.of()));
        ctx.register(HERBAL_TEA, new Drink(Drink.Type.TEA, List.of()));

        ctx.register(APPLE_JUICE, new Drink(Drink.Type.JUICE, List.of()));
        ctx.register(FRUIT_PUNCH, new Drink(Drink.Type.JUICE, List.of()));
    }

    private static DrinkEffect<MobEffectTemplate.Parameters> mobEffect(Holder<MobEffect> effect, int duration, int amplifier) {
        return new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(effect, duration, amplifier));
    }
}
