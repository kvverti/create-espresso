package systems.thedawn.espresso.client.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.drink.DrinkComponent;

/**
 * Drink condition template that always evaluates to the given value.
 */
public final class TrivialTemplate implements ConditionTemplate<Boolean> {
    public static final MapCodec<Boolean> PARAMS_CODEC = Codec.BOOL.fieldOf("value");

    @Override
    public boolean test(DrinkComponent drink, Boolean params) {
        return params;
    }

    @Override
    public MapCodec<Boolean> paramsCodec() {
        return PARAMS_CODEC;
    }
}
