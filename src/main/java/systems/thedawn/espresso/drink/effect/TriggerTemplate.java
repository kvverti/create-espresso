package systems.thedawn.espresso.drink.effect;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * A template that modifies a scoreboard trigger.
 */
public final class TriggerTemplate implements DrinkEffectTemplate<TriggerTemplate.Parameters> {
    @Override
    public void apply(Player drinker, int level, double strength, Parameters params) {
        var scoreboard = drinker.getCommandSenderWorld().getScoreboard();
        var objective = scoreboard.getObjective(params.objective());
        if(objective != null && objective.getCriteria() == ObjectiveCriteria.TRIGGER) {
            var score = scoreboard.getOrCreatePlayerScore(drinker, objective);
            switch(params.action()) {
                case ADD -> score.add(params.amount());
                case SET -> score.set(params.amount());
            }
        } else {
            LogUtils.getLogger().warn("Objective for drink effect does not exist or is not a trigger: {}", params.objective());
        }
    }

    @Override
    public MapCodec<Parameters> paramsCodec() {
        return Parameters.CODEC;
    }

    public record Parameters(String objective, Action action, int amount) {
        public static final MapCodec<Parameters> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("objective").forGetter(Parameters::objective),
            Action.CODEC.optionalFieldOf("action", Action.ADD).forGetter(Parameters::action),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(Parameters::amount)
        ).apply(inst, Parameters::new));
    }

    public enum Action implements StringRepresentable {
        ADD,
        SET;

        public static final Codec<Action> CODEC = StringRepresentable.fromEnum(Action::values);

        @Override
        public String getSerializedName() {
            return switch(this) {
                case ADD -> "add";
                case SET -> "set";
            };
        }
    }
}
