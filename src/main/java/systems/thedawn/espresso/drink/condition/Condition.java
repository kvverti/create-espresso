package systems.thedawn.espresso.drink.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.HolderLookup;

/**
 * A predicate that a drink component can be tested against. Used to determine
 * whether to show various model parts.
 *
 * @param template the template
 * @param params   specific instance of parameters
 * @param <P>      the type of parameters
 */
public record Condition<P>(ConditionTemplate<P> template, P params) {
    public static final Codec<Condition<?>> DIRECT_CODEC = EspressoRegistries.Static.DRINK_CONDITION_TEMPLATES
        .byNameCodec()
        .dispatch("template", Condition::template, Condition::paramsFromTemplate);

    private static <P> MapCodec<Condition<P>> paramsFromTemplate(ConditionTemplate<P> template) {
        return template.paramsCodec().xmap(p -> new Condition<>(template, p), Condition::params);
    }

    public boolean test(DrinkComponent drink, HolderLookup.Provider registries) {
        return this.template.test(drink, registries, this.params);
    }
}
