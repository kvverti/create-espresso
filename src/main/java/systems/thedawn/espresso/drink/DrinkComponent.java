package systems.thedawn.espresso.drink;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * Data that defines a complete drink. This includes the drink base as well as modifiers
 * that come from further processing.
 *
 * @param base  the base drink (pour over, cold brew, etc.)
 * @param level the level of the base drink
 */
public record DrinkComponent(Holder<Drink> base, BaseLevel level, List<Holder<DrinkModifier>> modifiers) {
    public static final Codec<DrinkComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Drink.CODEC.fieldOf("drink").forGetter(DrinkComponent::base),
        BaseLevel.CODEC.fieldOf("level").forGetter(DrinkComponent::level),
        DrinkModifier.CODEC.listOf().fieldOf("modifiers").forGetter(DrinkComponent::modifiers)
    ).apply(inst, DrinkComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkComponent> STREAM_CODEC = StreamCodec.composite(
        Drink.STREAM_CODEC,
        DrinkComponent::base,
        ByteBufCodecs.fromCodec(BaseLevel.CODEC),
        DrinkComponent::level,
        DrinkModifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
        DrinkComponent::modifiers,
        DrinkComponent::new
    );

    public static DrinkComponent initial(Holder<Drink> base) {
        return new DrinkComponent(base, BaseLevel.SINGLE, List.of());
    }

    public DrinkComponent incrementLevel() {
        var level = switch(this.level) {
            case SINGLE -> BaseLevel.DOUBLE;
            case DOUBLE, TRIPLE -> BaseLevel.TRIPLE;
        };
        return new DrinkComponent(this.base, level, this.modifiers);
    }

    public DrinkComponent addModifier(Holder<DrinkModifier> modifier) {
        var newModifiers = Stream.concat(this.modifiers.stream(), Stream.of(modifier)).toList();
        return new DrinkComponent(this.base, this.level, newModifiers);
    }

    public enum BaseLevel implements StringRepresentable {
        SINGLE,
        DOUBLE,
        TRIPLE;

        public static final Codec<BaseLevel> CODEC = StringRepresentable.fromEnum(BaseLevel::values);

        @Override
        public String getSerializedName() {
            return switch(this) {
                case SINGLE -> "single";
                case DOUBLE -> "double";
                case TRIPLE -> "triple";
            };
        }

        public String getDescriptionId() {
            return "create_espresso.drinkLevel." + this.getSerializedName();
        }
    }
}
