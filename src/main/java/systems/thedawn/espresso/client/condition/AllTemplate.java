package systems.thedawn.espresso.client.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.HolderLookup;

public final class AllTemplate implements ConditionTemplate<List<DeferredCondition>> {
    public static final MapCodec<List<DeferredCondition>> PARAMS_CODEC = DeferredCondition.CODEC.listOf().fieldOf("conditions");

    @Override
    public boolean test(DrinkComponent drink, HolderLookup.Provider registries, List<DeferredCondition> params) {
        var conditions = registries.lookupOrThrow(EspressoRegistries.DRINK_CONDITIONS);
        for(var deferredCondition : params) {
            var condition = deferredCondition.resolve(conditions);
            if(condition != null && !condition.test(drink, registries)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MapCodec<List<DeferredCondition>> paramsCodec() {
        return PARAMS_CODEC;
    }
}
