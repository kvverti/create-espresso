package systems.thedawn.espresso.client.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.DrinkComponent;
import systems.thedawn.espresso.drink.DrinkModifier;

import net.minecraft.resources.ResourceKey;

/**
 * A condition template that tests against a list of modifiers.
 */
public final class ModifierTemplate implements ConditionTemplate<List<ResourceKey<DrinkModifier>>> {
    public static final MapCodec<List<ResourceKey<DrinkModifier>>> PARAMS_CODEC =
        ResourceKey.codec(EspressoRegistries.DRINK_MODIFIERS).listOf().fieldOf("modifiers");

    @Override
    public boolean test(DrinkComponent drink, List<ResourceKey<DrinkModifier>> params) {
        for(var modifier : drink.modifiers()) {
            if(params.contains(modifier.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MapCodec<List<ResourceKey<DrinkModifier>>> paramsCodec() {
        return PARAMS_CODEC;
    }
}
