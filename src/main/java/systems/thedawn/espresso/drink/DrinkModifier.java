package systems.thedawn.espresso.drink;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.EspressoRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
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

    public static String getDescriptionId(ResourceKey<DrinkModifier> key) {
        return "create_espresso.modifier." + key.location().toLanguageKey();
    }

    public static Component getDescription(ResourceKey<DrinkModifier> key) {
        return Component.translatable(getDescriptionId(key)).withStyle(ChatFormatting.GRAY);
    }

    public sealed interface BaseTransform {
        Codec<BaseTransform> CODEC = StringRepresentable.fromEnum(Type::values)
            .dispatch(BaseTransform::type, type -> switch(type) {
                case LENGTHEN -> Codec.FLOAT.fieldOf("scale").xmap(Lengthen::new, Lengthen::scale);
                case STRENGTHEN -> Codec.INT.fieldOf("level").xmap(Strengthen::new, Strengthen::level);
            });

        Type type();

        record Lengthen(float scale) implements BaseTransform {
            @Override
            public Type type() {
                return Type.LENGTHEN;
            }
        }

        record Strengthen(int level) implements BaseTransform {
            @Override
            public Type type() {
                return Type.STRENGTHEN;
            }
        }

        enum Type implements StringRepresentable {
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
