package systems.thedawn.espresso.client.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

/**
 * A {@link Condition} that allows lazy references. Using this interface can lazily store a
 * resource key until it can be resolved with a registry lookup, or eagerly store an inline value.
 * If you don't want to store values inline, use ResourceKey directly.
 */
public sealed interface DeferredCondition {
    Codec<DeferredCondition> CODEC = Codec.either(
        ResourceKey.codec(EspressoRegistries.DRINK_CONDITIONS),
        Condition.DIRECT_CODEC
    ).xmap(either -> either.map(Indirect::new, Direct::new), deferredCondition -> switch(deferredCondition) {
        case Indirect(var key) -> Either.left(key);
        case Direct(var condition) -> Either.right(condition);
    });

    static <P> DeferredCondition direct(ConditionTemplate<P> template, P params) {
        return new Direct(new Condition<>(template, params));
    }

    static DeferredCondition indirect(ResourceKey<Condition<?>> key) {
        return new Indirect(key);
    }

    /**
     * Resolve this condition against its registry.
     *
     * @return the resolved condition, or null if the condition cannot be resolved
     */
    @Nullable Condition<?> resolve(HolderLookup.RegistryLookup<Condition<?>> registry);

    /**
     * Resolve and test the condition.
     *
     * @return the result of the test, or false if the condition cannot be resolved
     */
    default boolean test(DrinkComponent drink, HolderLookup.Provider registries) {
        var condition = this.resolve(registries.lookupOrThrow(EspressoRegistries.DRINK_CONDITIONS));
        return condition != null && condition.test(drink, registries);
    }

    record Direct(Condition<?> condition) implements DeferredCondition {
        @Override
        public Condition<?> resolve(HolderLookup.RegistryLookup<Condition<?>> registry) {
            return this.condition;
        }
    }

    record Indirect(ResourceKey<Condition<?>> key) implements DeferredCondition {
        @Override
        public Condition<?> resolve(HolderLookup.RegistryLookup<Condition<?>> registry) {
            return registry.get(this.key).map(Holder::value).orElse(null);
        }
    }
}
