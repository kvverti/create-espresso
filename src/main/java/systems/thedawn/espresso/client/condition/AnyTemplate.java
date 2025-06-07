package systems.thedawn.espresso.client.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.HolderLookup;

public final class AnyTemplate implements ConditionTemplate<List<DeferredCondition>> {
    @Override
    public boolean test(DrinkComponent drink, HolderLookup.Provider registries, List<DeferredCondition> params) {
        var conditions = registries.lookupOrThrow(EspressoRegistries.DRINK_CONDITIONS);
        for(var deferredCondition : params) {
            var condition = deferredCondition.resolve(conditions);
            if(condition != null && condition.test(drink, registries)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MapCodec<List<DeferredCondition>> paramsCodec() {
        return AllTemplate.PARAMS_CODEC;
    }
}
