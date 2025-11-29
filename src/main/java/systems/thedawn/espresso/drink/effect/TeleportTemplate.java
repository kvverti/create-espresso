package systems.thedawn.espresso.drink.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

/**
 * A template that defines teleportation randomly within a radius.
 */
public final class TeleportTemplate implements DrinkEffectTemplate<Integer> {
    public static final MapCodec<Integer> PARAMS_CODEC = Codec.intRange(1, 100).fieldOf("range");

    @Override
    public void apply(Player drinker, int level, double strength, Integer params) {
        var teleportRadius = (int) (strength * params);
        var rand = drinker.level().getRandom();
        var dx = rand.nextIntBetweenInclusive(-teleportRadius, teleportRadius);
        var dz = rand.nextIntBetweenInclusive(-teleportRadius, teleportRadius);
        drinker.randomTeleport(dx, drinker.getY(), dz, false);
    }

    @Override
    public MapCodec<Integer> paramsCodec() {
        return PARAMS_CODEC;
    }
}
