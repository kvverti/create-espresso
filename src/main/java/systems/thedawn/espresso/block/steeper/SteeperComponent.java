package systems.thedawn.espresso.block.steeper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * The information important to holding a steeper as an item.
 *
 * @param drinkFluid the fluid in the steeper
 * @param dregs      the items in the steeper
 */
public record SteeperComponent(FluidStack drinkFluid, ItemStack dregs) {
    public static final Codec<SteeperComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        FluidStack.OPTIONAL_CODEC.fieldOf("drink").forGetter(SteeperComponent::drinkFluid),
        ItemStack.OPTIONAL_CODEC.fieldOf("dregs").forGetter(SteeperComponent::dregs)
    ).apply(inst, SteeperComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SteeperComponent> STREAM_CODEC = StreamCodec.composite(
        FluidStack.OPTIONAL_STREAM_CODEC,
        SteeperComponent::drinkFluid,
        ItemStack.OPTIONAL_STREAM_CODEC,
        SteeperComponent::dregs,
        SteeperComponent::new
    );

    public static SteeperComponent empty() {
        return new SteeperComponent(FluidStack.EMPTY, ItemStack.EMPTY);
    }
}
