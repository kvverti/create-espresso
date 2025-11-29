package systems.thedawn.espresso.drink.effect;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

/**
 * An effect to be performed when a player drinks a drink. Effects may be added by the base drink or by modifiers.
 * 
 * In addition to template-provided configuration, effects are governed by these variables.
 * - the level of the drink, a positive integer value. A drink's level can be changed by leveling or modifying a drink.
 * - the strength modifier of the drink, a positive floating-point value. A drink's strength can be changed by modifying the drink.
 * 
 * @param <P> parameters for this effect
 */
public interface DrinkEffectTemplate<P> {
    /**
     * Applies this effect to the given player.
     * 
     * @param drinker the player who drank a drink with this effect
     * @param level the level of the drink
     * @param strength the strength of the drink
     * @param params parameters for this effect
     */
    void apply(Player drinker, int level, double strength, P params);

    /**
     * Parameters codec for this effect.
     */
    MapCodec<P> paramsCodec();
}
