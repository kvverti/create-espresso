package systems.thedawn.espresso.client.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.drink.DrinkComponent;

public final class AnyTemplate implements ConditionTemplate<List<ConditionHolder>> {
    @Override
    public boolean test(DrinkComponent drink, List<ConditionHolder> params) {
        for(var condition : params) {
            if(condition.resolve().test(drink)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MapCodec<List<ConditionHolder>> paramsCodec() {
        return AllTemplate.PARAMS_CODEC;
    }
}
