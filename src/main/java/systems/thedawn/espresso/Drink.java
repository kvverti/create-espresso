package systems.thedawn.espresso;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.effect.MobEffectInstance;

/**
 * A drink.
 *
 * @param effects the potion effects applied after drinking
 */
public record Drink(List<MobEffectInstance> effects) {
    public static final Codec<Drink> CODEC =
        RecordCodecBuilder.create(inst -> inst.group(
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(Drink::effects)
        ).apply(inst, Drink::new));
}
