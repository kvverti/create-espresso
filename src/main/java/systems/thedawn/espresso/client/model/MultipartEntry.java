package systems.thedawn.espresso.client.model;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.drink.DrinkComponent;
import systems.thedawn.espresso.drink.condition.DeferredCondition;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

/**
 * A single multipart entry for a dynamic drink model.
 *
 * @param condition the condition necessary to include the model
 * @param selector  the selector for the model
 */
public record MultipartEntry(DeferredCondition condition, ModelSelector selector) {
    public static final Codec<MultipartEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        DeferredCondition.CODEC.fieldOf("when").forGetter(MultipartEntry::condition),
        ModelSelector.CODEC.fieldOf("apply").forGetter(MultipartEntry::selector)
    ).apply(inst, MultipartEntry::new));

    /**
     * Selects a single model according to the given drink.
     */
    public Optional<ResourceLocation> selectModel(DrinkComponent drink, HolderLookup.Provider registries) {
        if(this.condition.test(drink, registries)) {
            switch(this.selector) {
                case ModelSelector.Single(var loc) -> {
                    return Optional.of(loc);
                }
                case ModelSelector.Alternatives(var alternatives) -> {
                    for(var alternative : alternatives) {
                        if(alternative.condition().test(drink, registries)) {
                            return Optional.of(alternative.modelLocation());
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
