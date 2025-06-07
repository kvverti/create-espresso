package systems.thedawn.espresso.client.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.drink.DrinkComponent;

public final class AllTemplate implements ConditionTemplate<List<ConditionHolder>> {
    public static final MapCodec<List<ConditionHolder>> PARAMS_CODEC = ConditionHolder.CODEC.listOf().fieldOf("conditions");

    @Override
    public boolean test(DrinkComponent drink, List<ConditionHolder> params) {
        for(var condition : params) {
            if(!condition.resolve().test(drink)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MapCodec<List<ConditionHolder>> paramsCodec() {
        return PARAMS_CODEC;
    }
}
