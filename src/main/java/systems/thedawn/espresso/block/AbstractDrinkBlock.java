package systems.thedawn.espresso.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.EspressoDataComponentTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;

/**
 * Base class for all drink holding blocks.
 */
public abstract class AbstractDrinkBlock extends TransparentBlock implements EntityBlock {
    public static final Property<Boolean> HAS_DRINK = BooleanProperty.create("has_drink");

    protected AbstractDrinkBlock(Properties properties) {
        super(properties);
    }

    /**
     * Override to add new properties. Must call super.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_DRINK);
    }

    /**
     * The item representing the empty version of the block.
     */
    public abstract Item getEmptyItem();

    /**
     * The item representing the filled version of the block.
     */
    public abstract Item getFilledItem();

    /**
     * Override to set new properties. Must call super.
     */
    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        var hasDrink = context.getItemInHand().has(EspressoDataComponentTypes.DRINK);
        return this.defaultBlockState().setValue(HAS_DRINK, hasDrink);
    }

    /**
     * Creates a drink block entity iff the block state indicates a drink is present.
     */
    @Override
    public final @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if(state.getValue(HAS_DRINK)) {
            return new DrinkBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public final ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var stack = new ItemStack(state.getValue(HAS_DRINK) ? this.getFilledItem() : this.getEmptyItem());
        level.getBlockEntity(pos, EspressoBlockEntityTypes.DRINK.value())
            .ifPresent(be -> stack.applyComponents(be.collectComponents()));
        return stack;
    }
}
