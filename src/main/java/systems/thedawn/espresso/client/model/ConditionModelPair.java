package systems.thedawn.espresso.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.client.condition.ConditionHolder;

import net.minecraft.resources.ResourceLocation;

/**
 * A pair of condition, model location.
 */
public record ConditionModelPair(ConditionHolder condition, ResourceLocation modelLocation) {
    public static final Codec<ConditionModelPair> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        ConditionHolder.CODEC.fieldOf("condition").forGetter(ConditionModelPair::condition),
        ResourceLocation.CODEC.fieldOf("model").forGetter(ConditionModelPair::modelLocation)
    ).apply(inst, ConditionModelPair::new));
}
