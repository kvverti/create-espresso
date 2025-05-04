package systems.thedawn.espresso;

import java.util.List;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    static void bootstrapDrinks(BootstrapContext<Drink> ctx) {
        ctx.register(EMPTY, Drink.EMPTY);
        ctx.register(DIRTY_COLD_BREW, new Drink(Drink.Type.COFFEE, List.of(
            new MobEffectInstance(MobEffects.POISON, 100)
        )));
        ctx.register(COLD_BREW, new Drink(Drink.Type.COFFEE, List.of()));
        ctx.register(POUR_OVER, new Drink(Drink.Type.COFFEE, List.of()));
        ctx.register(ESPRESSO, new Drink(Drink.Type.COFFEE, List.of()));
    }
}
