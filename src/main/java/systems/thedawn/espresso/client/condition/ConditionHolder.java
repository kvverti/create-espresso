package systems.thedawn.espresso.client.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

/**
 * Store for either a value or a reference into the conditions client registry.
 */
public sealed interface ConditionHolder {
    Codec<ConditionHolder> CODEC = Codec.either(
        ResourceLocation.CODEC,
        Condition.DIRECT_CODEC
    ).xmap(either -> either.map(Indirect::new, Direct::new), deferredCondition -> switch(deferredCondition) {
        case Indirect indirect -> Either.left(indirect.key);
        case Direct(var condition) -> Either.right(condition);
    });

    static <P> ConditionHolder direct(ConditionTemplate<P> template, P params) {
        return new Direct(new Condition<>(template, params));
    }

    static ConditionHolder indirect(ResourceLocation key) {
        return new Indirect(key);
    }

    /**
     * Resolve this condition against its registry.
     */
    Condition<?> resolve();

    record Direct(Condition<?> condition) implements ConditionHolder {
        @Override
        public Condition<?> resolve() {
            return this.condition;
        }
    }

    final class Indirect implements ConditionHolder {
        private final ResourceLocation key;
        private transient @Nullable Condition<?> value;

        public Indirect(ResourceLocation key) {
            this.key = key;
        }

        @Override
        public Condition<?> resolve() {
            if(this.value == null) {
                this.value = ConditionManager.getCondition(this.key);
                if(this.value == null) {
                    throw new IllegalStateException("Indirect condition with no value: " + this.key);
                }
            }

            return this.value;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(!(obj instanceof Indirect other)) {
                return false;
            }
            return this.key.equals(other.key);
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public String toString() {
            return "Indirect[" + this.key + "]";
        }
    }
}
