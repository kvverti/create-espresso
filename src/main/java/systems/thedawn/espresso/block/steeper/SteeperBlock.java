package systems.thedawn.espresso.block.steeper;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

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
                    FluidStack resultFluid = null;
                    ItemStack remainingItem = null;

                    var recipe = this.getEmptyingRecipe(stack, level);
                    if(recipe != null) {
                        resultFluid = recipe.getResultingFluid();
                        remainingItem = recipe.getResultItem(level.registryAccess());
                    } else if(GenericItemEmptying.canItemBeEmptied(level, stack)) {
                        var result = GenericItemEmptying.emptyItem(level, stack, true);
                        if(result.getFirst().getAmount() == FLUID_CAPACITY) {
                            resultFluid = result.getFirst();
                            remainingItem = result.getSecond();
                        }
                    }

                    if(resultFluid != null) {
                        if(!level.isClientSide()) {
                            be.fillWithFluid(resultFluid);
                            player.setItemInHand(hand, remainingItem);
                        }
                        player.gameEvent(GameEvent.BLOCK_CHANGE);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                } else {
                    // check Create filling recipes for extracting fluid
                    var filledFluid = be.getFilledFluid();
                    ItemStack filledStack = null;

                    var recipe = this.getFillingRecipe(stack, filledFluid, level);
                    if(recipe != null) {
                        filledStack = recipe.getResultItem(level.registryAccess());
                    } else if(GenericItemFilling.canItemBeFilled(level, stack) &&
                        GenericItemFilling.getRequiredAmountForItem(level, stack, filledFluid) == SteeperBlock.FLUID_CAPACITY) {
                        filledStack = GenericItemFilling.fillItem(level, SteeperBlock.FLUID_CAPACITY, stack.copy(), filledFluid);
                    }

                    if(filledStack != null) {
                        if(!level.isClientSide()) {
                            be.drainFluid();
                            player.setItemInHand(hand, filledStack);
                        }
                        player.gameEvent(GameEvent.BLOCK_CHANGE);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                }

                if(be.canPlaceItem(stack)) {
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

    private @Nullable EmptyingRecipe getEmptyingRecipe(ItemStack stack, Level level) {
        var input = new SingleRecipeInput(stack);
        return level.getRecipeManager()
            .getRecipeFor(AllRecipeTypes.EMPTYING.<SingleRecipeInput, EmptyingRecipe>getType(), input, level)
            .map(RecipeHolder::value)
            .filter(recipe -> recipe.getResultingFluid().getAmount() == FLUID_CAPACITY)
            .orElse(null);
    }

    private @Nullable FillingRecipe getFillingRecipe(ItemStack stack, FluidStack fluid, Level level) {
        var recipes = level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.FILLING.<SingleRecipeInput, FillingRecipe>getType());
        for(var holder : recipes) {
            var recipe = holder.value();
            if(recipe.getRequiredFluid().test(fluid) && recipe.getIngredients().get(0).test(stack)) {
                return recipe;
            }
        }
        return null;
    }
}
