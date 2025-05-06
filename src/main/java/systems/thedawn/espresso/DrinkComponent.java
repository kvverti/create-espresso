package systems.thedawn.espresso;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DrinkComponent(Holder<Drink> drink) {
    public static final Codec<DrinkComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Drink.CODEC.fieldOf("drink").forGetter(DrinkComponent::drink)
    ).apply(inst, DrinkComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkComponent> STREAM_CODEC = StreamCodec.composite(
        Drink.STREAM_CODEC,
        DrinkComponent::drink,
        DrinkComponent::new
    );
}
