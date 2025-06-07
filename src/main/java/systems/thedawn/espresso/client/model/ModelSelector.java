package systems.thedawn.espresso.client.model;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;

/**
 * A model or cascading list of alternative models.
 */
public sealed interface ModelSelector {
    Codec<ModelSelector> CODEC = Codec.xor(
        ResourceLocation.CODEC,
        ConditionModelPair.CODEC.listOf()
    ).xmap(either -> either.map(Single::new, Alternatives::new), selector -> switch(selector) {
        case Single(var modelLocation) -> Either.left(modelLocation);
        case Alternatives(var alternatives) -> Either.right(alternatives);
    });

    static ModelSelector single(ResourceLocation modelLocation) {
        return new Single(modelLocation);
    }

    static ModelSelector alternatives(ConditionModelPair... alternatives) {
        return new Alternatives(List.of(alternatives));
    }

    void forEachModel(Consumer<ResourceLocation> task);

    record Single(ResourceLocation modelLocation) implements ModelSelector {
        @Override
        public void forEachModel(Consumer<ResourceLocation> task) {
            task.accept(this.modelLocation);
        }
    }

    record Alternatives(List<ConditionModelPair> alternatives) implements ModelSelector {
        @Override
        public void forEachModel(Consumer<ResourceLocation> task) {
            for(var alternative : this.alternatives) {
                task.accept(alternative.modelLocation());
            }
        }
    }
}
