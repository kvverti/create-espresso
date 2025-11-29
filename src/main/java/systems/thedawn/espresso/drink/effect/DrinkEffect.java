package systems.thedawn.espresso.drink.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;
import systems.thedawn.espresso.EspressoRegistries;

/**
 * A configured action that is performed when a player drinks a drink.
 */
public record DrinkEffect<P>(DrinkEffectTemplate<P> template, P params) {
    public static final Codec<DrinkEffect<?>> CODEC = EspressoRegistries.Static.DRINK_EFFECT_TEMPLATES
        .byNameCodec()
        .dispatch("template", DrinkEffect::template, DrinkEffect::codecFromTemplate);

    private static <P> MapCodec<DrinkEffect<P>> codecFromTemplate(DrinkEffectTemplate<P> template) {
        return template.paramsCodec().xmap(p -> new DrinkEffect<>(template, p), DrinkEffect::params);
    }

    public void apply(Player drinker, int level, double strength) {
        this.template.apply(drinker, level, strength, this.params);
    }
}
