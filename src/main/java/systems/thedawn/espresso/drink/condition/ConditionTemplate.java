package systems.thedawn.espresso.drink.condition;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.HolderLookup;

/**
 * A template for drink conditions that defines a set of parameters
 * and an interpretation of those parameters.
 * <p>
 * Note that condition templates must be valid without a dynamic registry context - they cannot
 * require lookup of dynamic registry objects. If you're referencing a {@link net.minecraft.core.Holder}
 * you're most likely doing it wrong.
 *
 * @param <P> parameters for this condition.
 */
public interface ConditionTemplate<P> {
    /**
     * Test the given drink against this condition.
     */
    boolean test(DrinkComponent drink, HolderLookup.Provider registries, P params);

    /**
     * The Codec for this condition's parameters.
     */
    MapCodec<P> paramsCodec();
}
