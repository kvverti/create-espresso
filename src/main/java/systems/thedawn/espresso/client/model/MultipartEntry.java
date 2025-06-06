package systems.thedawn.espresso.client.model;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.drink.condition.DeferredCondition;

/**
 * A single multipart entry for a dynamic drink model.
 *
 * @param condition    the condition necessary to include the model
 * @param alternatives a cascading list of alternative models
 */
public record MultipartEntry(DeferredCondition condition, List<ConditionModelPair> alternatives) {
    public static final Codec<MultipartEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        DeferredCondition.CODEC.fieldOf("when").forGetter(MultipartEntry::condition),
        ConditionModelPair.CODEC.listOf().fieldOf("alternatives").forGetter(MultipartEntry::alternatives)
    ).apply(inst, MultipartEntry::new));
}
