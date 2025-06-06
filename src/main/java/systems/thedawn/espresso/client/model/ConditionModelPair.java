package systems.thedawn.espresso.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.drink.condition.DeferredCondition;

import net.minecraft.resources.ResourceLocation;

/**
 * A pair of condition, model location.
 */
public record ConditionModelPair(DeferredCondition condition, ResourceLocation modelLocation) {
    public static final Codec<ConditionModelPair> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        DeferredCondition.CODEC.fieldOf("condition").forGetter(ConditionModelPair::condition),
        ResourceLocation.CODEC.fieldOf("model").forGetter(ConditionModelPair::modelLocation)
    ).apply(inst, ConditionModelPair::new));
}
