package systems.thedawn.espresso.drink.condition;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoRegistries;

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
        ResourceKey.codec(EspressoRegistries.DRINK_CONDITIONS).xmap(Indirect::new, Indirect::key),
        Condition.DIRECT_CODEC.xmap(Direct::new, Direct::condition)
    ).xmap(either -> either.map(Function.identity(), Function.identity()), deferredCondition -> switch(deferredCondition) {
        case Indirect indirect -> Either.left(indirect);
        case Direct direct -> Either.right(direct);
    });

    /**
     * Resolve this condition against its registry.
     *
     * @return the resolved condition, or null if the condition cannot be resolved
     */
    @Nullable Condition<?> resolve(HolderLookup.RegistryLookup<Condition<?>> registry);

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
