package systems.thedawn.espresso.drink;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.effect.DrinkEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;

/**
 * A modifier placed on a drink, such as milk, ice, or bubbles.
 */
public record DrinkModifier(int levelOffset, double strengthScale, List<DrinkEffect<?>> additionalEffects) {
    public static final Codec<DrinkModifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.intRange(-10, 10).optionalFieldOf("offset_levels", 0).forGetter(DrinkModifier::levelOffset),
        Codec.doubleRange(0.01, 10).optionalFieldOf("strength_multiplier", 1d).forGetter(DrinkModifier::strengthScale),
        DrinkEffect.CODEC.listOf().optionalFieldOf("additional_effects", List.of()).forGetter(DrinkModifier::additionalEffects)
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
}
