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
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.POISON, DrinkUtil.seconds(5), 0)),
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.CONFUSION, DrinkUtil.seconds(5), 0))
        )));
        ctx.register(COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.MOVEMENT_SPEED, DrinkUtil.minutes(1), 0))
        )));
        ctx.register(POUR_OVER, new Drink(Drink.Type.COFFEE, List.of(
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.MOVEMENT_SPEED, DrinkUtil.minutes(1) + DrinkUtil.seconds(15), 0)),
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.NIGHT_VISION, DrinkUtil.minutes(1) + DrinkUtil.seconds(15), 0))
        )));
        ctx.register(ESPRESSO, new Drink(Drink.Type.COFFEE, List.of(
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.MOVEMENT_SPEED, DrinkUtil.minutes(2) + DrinkUtil.seconds(30), 0)),
            new DrinkEffect<>(EspressoDrinkEffectTemplates.MOB_EFFECT.value(), new MobEffectTemplate.Parameters(MobEffects.REGENERATION, DrinkUtil.minutes(2) + DrinkUtil.seconds(30), 0))
        )));
    }
}
