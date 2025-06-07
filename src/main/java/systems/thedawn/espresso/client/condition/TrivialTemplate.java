package systems.thedawn.espresso.client.condition;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.util.Unit;

/**
 * Drink condition template that always succeeds.
 */
public final class TrivialTemplate implements ConditionTemplate<Unit> {
    public static final MapCodec<Unit> PARAMS_CODEC = MapCodec.unit(Unit.INSTANCE);

    @Override
    public boolean test(DrinkComponent drink, Unit params) {
        return true;
    }

    @Override
    public MapCodec<Unit> paramsCodec() {
        return PARAMS_CODEC;
    }
}
