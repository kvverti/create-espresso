package systems.thedawn.espresso.block.steeper;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.EspressoTags;
import systems.thedawn.espresso.util.RecipeUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SteeperBlock extends TransparentBlock implements EntityBlock {
    public static final int FLUID_CAPACITY = 250;

    public SteeperBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteeperBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(blockEntityType == EspressoBlockEntityTypes.STEEPER.value()) {
            return (level1, pos, state1, blockEntity) -> ((SteeperBlockEntity) blockEntity).tick();
        }
        return EntityBlock.super.getTicker(level, state, blockEntityType);
    }

    private static final VoxelShape SHAPE = Shapes.box(3f / 16f, 0f, 3f / 16f, 13f / 16f, 13f / 16f, 13f / 16f);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * Empties a fluid vessel, fills an empty fluid vessel, takes items, or inserts an item into the steeper.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var be = level.getBlockEntity(pos, EspressoBlockEntityTypes.STEEPER.value()).orElse(null);
        if(be != null) {
            if(!stack.isEmpty()) {
                // check Create emptying recipes for depositing fluid
                if(be.canFillWithFluid()) {
                    var result = RecipeUtil.findEmptyingResult(stack, level);
                    if(result != null && result.resultFluid().is(EspressoTags.STEEPER_ENABLED_FLUIDS) && result.resultFluid().getAmount() == FLUID_CAPACITY) {
                        if(!level.isClientSide()) {
                            be.fillWithFluid(result.resultFluid());
                            player.setItemInHand(hand, result.remainingItem());
                        }
                        player.gameEvent(GameEvent.BLOCK_CHANGE);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                } else {
                    // check Create filling recipes for extracting fluid
                    var result = RecipeUtil.findFillingResult(stack, level, be.getFilledFluid(), FLUID_CAPACITY);
                    if(result != null && result.requiredFluidAmount() == FLUID_CAPACITY) {
                        if(!level.isClientSide()) {
                            be.drainFluid();
                            player.setItemInHand(hand, result.resultStack());
                        }
                        player.gameEvent(GameEvent.BLOCK_CHANGE);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                }

                if(stack.is(EspressoTags.STEEPER_ENABLED_ITEMS) && be.canPlaceItem(stack)) {
                    if(!level.isClientSide()) {
                        var placed = stack.copy();
                        placed.setCount(1);
                        stack.shrink(1);
                        be.placeItem(placed);
                    }
                    player.gameEvent(GameEvent.BLOCK_CHANGE);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }
            } else if(be.hasItems()) {
                // take items
                if(!level.isClientSide()) {
                    var items = be.takeItems();
                    player.setItemInHand(hand, items);
                }
                player.gameEvent(GameEvent.BLOCK_CHANGE);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        var drops = super.getDrops(state, params);
        if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SteeperBlockEntity steeper) {
            var dregs = steeper.getDregs();
            if(!dregs.isEmpty()) {
                drops.add(dregs);
            }
        }
        return drops;
    }
}
