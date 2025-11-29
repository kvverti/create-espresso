package systems.thedawn.espresso.drink.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

/**
 * A template that applies a mob effect to the drinker.
 */
public final class MobEffectTemplate implements DrinkEffectTemplate<MobEffectTemplate.Parameters> {
    @Override
    public void apply(Player drinker, int level, double strength, Parameters params) {
        var durationScale = Math.min(strength, 3);
        var amplifierOffset = Math.min(level - 1, 5);
        var effectInstance = new MobEffectInstance(params.effect(), (int) (durationScale * params.duration()), amplifierOffset + params.amplifier());
        drinker.addEffect(effectInstance);
    }

    @Override
    public MapCodec<Parameters> paramsCodec() {
        return Parameters.CODEC;
    }

    public record Parameters(Holder<MobEffect> effect, int duration, int amplifier) {
        public static final MapCodec<Parameters> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            MobEffect.CODEC.fieldOf("id").forGetter(Parameters::effect),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("duration").forGetter(Parameters::duration),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(Parameters::amplifier)
        ).apply(inst, Parameters::new));
    }
}
