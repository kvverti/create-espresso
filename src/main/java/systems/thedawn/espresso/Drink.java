package systems.thedawn.espresso;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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
 * A drink.
 *
 * @param effects the potion effects applied after drinking
 */
public record Drink(Type type, List<MobEffectInstance> effects) {
    public static final Drink EMPTY = new Drink(Type.COFFEE, List.of());

    public static final Codec<Drink> DIRECT_CODEC =
        RecordCodecBuilder.create(inst -> inst.group(
            StringRepresentable.fromEnum(Type::values).fieldOf("type").forGetter(Drink::type),
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(Drink::effects)
        ).apply(inst, Drink::new));
    public static final Codec<Holder<Drink>> CODEC =
        RegistryFileCodec.create(EspressoRegistries.DRINKS, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Drink>> STREAM_CODEC =
        ByteBufCodecs.holderRegistry(EspressoRegistries.DRINKS);

    public static String getDescriptionId(ResourceKey<Drink> key) {
        return "create_espresso.drink." + key.location().toLanguageKey();
    }

    public static Component getDescription(ResourceKey<Drink> key) {
        return Component.translatable(getDescriptionId(key)).withStyle(ChatFormatting.GRAY);
    }

    public enum Type implements StringRepresentable {
        COFFEE,
        TEA;

        @Override
        public String getSerializedName() {
            return switch(this) {
                case COFFEE -> "coffee";
                case TEA -> "tea";
            };
        }
    }
}
