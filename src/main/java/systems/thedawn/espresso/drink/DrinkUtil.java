package systems.thedawn.espresso.drink;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class DrinkUtil {
    static int seconds(int seconds) {
        return seconds * 20;
    }

    static int minutes(int minutes) {
        return seconds(minutes * 60);
    }

    /**
     * Construct an effect instance without the default neoforge:cures component since
     * it's non-determinstic and unnecessary to serialize.
     */
    static MobEffectInstance effectInstance(Holder<MobEffect> effect, int duration) {
        var instance = new MobEffectInstance(effect, duration);
        instance.getCures().clear();
        return instance;
    }
}
