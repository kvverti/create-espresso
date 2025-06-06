package systems.thedawn.espresso.drink.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

/**
 * A condition template that tests against a list of drink bases.
 */
public final class DrinkTemplate implements ConditionTemplate<List<ResourceKey<Drink>>> {
    public static final MapCodec<List<ResourceKey<Drink>>> PARAMS_CODEC =
        ResourceKey.codec(EspressoRegistries.DRINKS).listOf().fieldOf("drinks");

    @Override
    public boolean test(DrinkComponent drink, HolderLookup.Provider registries, List<ResourceKey<Drink>> params) {
        return params.contains(drink.base().getKey());
    }

    @Override
    public MapCodec<List<ResourceKey<Drink>>> paramsCodec() {
        return PARAMS_CODEC;
    }
}
