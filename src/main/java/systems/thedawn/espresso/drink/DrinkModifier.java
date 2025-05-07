package systems.thedawn.espresso.drink;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.EspressoRegistries;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * A modifier placed on a drink, such as milk, ice, or bubbles.
 */
public record DrinkModifier(Optional<BaseTransform> transform, List<MobEffectInstance> additionalEffects) {
    public static final Codec<DrinkModifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        BaseTransform.CODEC.optionalFieldOf("transform").forGetter(DrinkModifier::transform),
        MobEffectInstance.CODEC.listOf().optionalFieldOf("additional_effects", List.of()).forGetter(DrinkModifier::additionalEffects)
    ).apply(inst, DrinkModifier::new));
    public static final Codec<Holder<DrinkModifier>> CODEC =
        RegistryFileCodec.create(EspressoRegistries.DRINK_MODIFIERS, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DrinkModifier>> STREAM_CODEC =
        ByteBufCodecs.holderRegistry(EspressoRegistries.DRINK_MODIFIERS);

    public record BaseTransform(Type type, float scale) {
        public static final Codec<BaseTransform> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            StringRepresentable.fromEnum(Type::values).fieldOf("type").forGetter(BaseTransform::type),
            Codec.FLOAT.fieldOf("scale").forGetter(BaseTransform::scale)
        ).apply(inst, BaseTransform::new));

        public enum Type implements StringRepresentable {
            LENGTHEN,
            STRENGTHEN;

            @Override
            public String getSerializedName() {
                return switch(this) {
                    case LENGTHEN -> "lengthen";
                    case STRENGTHEN -> "strengthen";
                };
            }
        }
    }
}
