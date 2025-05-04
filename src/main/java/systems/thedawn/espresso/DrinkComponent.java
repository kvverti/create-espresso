package systems.thedawn.espresso;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record DrinkComponent(ResourceLocation type) {
    public static final Codec<DrinkComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        ResourceLocation.CODEC.fieldOf("type").forGetter(DrinkComponent::type)
    ).apply(inst, DrinkComponent::new));

    public static final StreamCodec<ByteBuf, DrinkComponent> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        DrinkComponent::type,
        DrinkComponent::new
    );
}
