package systems.thedawn.espresso.drink;

import java.util.List;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoRegistries;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class BuiltinEspressoDrinks {
    public static final ResourceKey<Drink> EMPTY =
        ResourceKey.create(EspressoRegistries.DRINKS, modLoc("empty"));
    public static final ResourceKey<Drink> DIRTY_COLD_BREW =
        ResourceKey.create(EspressoRegistries.DRINKS, modLoc("dirty_cold_brew"));
    public static final ResourceKey<Drink> COLD_BREW =
        ResourceKey.create(EspressoRegistries.DRINKS, modLoc("cold_brew"));
    public static final ResourceKey<Drink> POUR_OVER =
        ResourceKey.create(EspressoRegistries.DRINKS, modLoc("pour_over"));
    public static final ResourceKey<Drink> ESPRESSO =
        ResourceKey.create(EspressoRegistries.DRINKS, modLoc("espresso"));

    private static ResourceLocation modLoc(String name) {
        return ResourceLocation.fromNamespaceAndPath(Espresso.MODID, name);
    }

    public static void bootstrapDrinks(BootstrapContext<Drink> ctx) {
        ctx.register(EMPTY, Drink.EMPTY);
        ctx.register(DIRTY_COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            effectInstance(MobEffects.POISON, seconds(5)),
            effectInstance(MobEffects.CONFUSION, seconds(5))
        )));
        ctx.register(COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            effectInstance(MobEffects.MOVEMENT_SPEED, minutes(1))
        )));
        ctx.register(POUR_OVER, new Drink(Drink.Type.COFFEE, List.of(
            effectInstance(MobEffects.MOVEMENT_SPEED, minutes(1) + seconds(15)),
            effectInstance(MobEffects.NIGHT_VISION, minutes(1) + seconds(15))
        )));
        ctx.register(ESPRESSO, new Drink(Drink.Type.COFFEE, List.of(
            effectInstance(MobEffects.MOVEMENT_SPEED, minutes(2) + seconds(30)),
            effectInstance(MobEffects.REGENERATION, minutes(2) + seconds(30))
        )));
    }

    private static int seconds(int seconds) {
        return seconds * 20;
    }

    private static int minutes(int minutes) {
        return seconds(minutes * 60);
    }

    /**
     * Construct an effect instance without the default neoforge:cures component since
     * it's non-determinstic and unnecessary to serialize.
     */
    private static MobEffectInstance effectInstance(Holder<MobEffect> effect, int duration) {
        var instance = new MobEffectInstance(effect, duration);
        instance.getCures().clear();
        return instance;
    }
}
