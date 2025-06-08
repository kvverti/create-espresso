package systems.thedawn.espresso.drink;

import java.util.List;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoRegistries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;

public final class BuiltinEspressoDrinks {
    public static final ResourceKey<Drink> EMPTY
        = ResourceKey.create(EspressoRegistries.DRINKS, Espresso.modLoc("empty"));
    public static final ResourceKey<Drink> DIRTY_COLD_BREW
        = ResourceKey.create(EspressoRegistries.DRINKS, Espresso.modLoc("dirty_cold_brew"));
    public static final ResourceKey<Drink> COLD_BREW
        = ResourceKey.create(EspressoRegistries.DRINKS, Espresso.modLoc("cold_brew"));
    public static final ResourceKey<Drink> POUR_OVER
        = ResourceKey.create(EspressoRegistries.DRINKS, Espresso.modLoc("pour_over"));
    public static final ResourceKey<Drink> ESPRESSO
        = ResourceKey.create(EspressoRegistries.DRINKS, Espresso.modLoc("espresso"));

    public static void bootstrapDrinks(BootstrapContext<Drink> ctx) {
        ctx.register(EMPTY, Drink.EMPTY);
        ctx.register(DIRTY_COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            DrinkUtil.effectInstance(MobEffects.POISON, DrinkUtil.seconds(5)),
            DrinkUtil.effectInstance(MobEffects.CONFUSION, DrinkUtil.seconds(5))
        )));
        ctx.register(COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            DrinkUtil.effectInstance(MobEffects.MOVEMENT_SPEED, DrinkUtil.minutes(1))
        )));
        ctx.register(POUR_OVER, new Drink(Drink.Type.COFFEE, List.of(
            DrinkUtil.effectInstance(MobEffects.MOVEMENT_SPEED, DrinkUtil.minutes(1) + DrinkUtil.seconds(15)),
            DrinkUtil.effectInstance(MobEffects.NIGHT_VISION, DrinkUtil.minutes(1) + DrinkUtil.seconds(15))
        )));
        ctx.register(ESPRESSO, new Drink(Drink.Type.COFFEE, List.of(
            DrinkUtil.effectInstance(MobEffects.MOVEMENT_SPEED, DrinkUtil.minutes(2) + DrinkUtil.seconds(30)),
            DrinkUtil.effectInstance(MobEffects.REGENERATION, DrinkUtil.minutes(2) + DrinkUtil.seconds(30))
        )));
    }
}
