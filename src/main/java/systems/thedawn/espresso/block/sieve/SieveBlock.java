package systems.thedawn.espresso.block.sieve;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.recipe.FilterCondition;
import systems.thedawn.espresso.util.ItemHandlerUtil;
import systems.thedawn.espresso.util.RecipeUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SieveBlock extends Block implements IBE<SieveBlockEntity>, IWrenchable {
    public static final Property<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final Property<FilterCondition> FILTER = EnumProperty.create("filter", FilterCondition.class);

    public SieveBlock(Properties properties) {
        super(properties);
    }

    private static final MapCodec<SieveBlock> CODEC = simpleCodec(SieveBlock::new);

    @Override
    protected MapCodec<SieveBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, FILTER);
    }

    public static boolean hasFilterEntry(BlockState state, Direction side) {
        return side.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Class<SieveBlockEntity> getBlockEntityClass() {
        return SieveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SieveBlockEntity> getBlockEntityType() {
        return EspressoBlockEntityTypes.SIEVE.value();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        this.withBlockEntityDo(level, pos, sieve -> {
            if(sieve.isRunningPassiveRecipe()) {
                var possibleItems = ItemHandlerUtil.nonEmptyContents(sieve.upperInventory());
                if(!possibleItems.isEmpty()) {
                    var slot = level.getRandom().nextInt(possibleItems.size());
                    var particle = new ItemParticleOption(ParticleTypes.ITEM, possibleItems.get(slot));
                    ParticleUtils.spawnParticles(level, pos, 1, 0.5, 0.25, true, particle);
                }
            }
        });
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch(rotation) {
            case NONE, CLOCKWISE_180 -> state;
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.cycle(AXIS);
        };
    }

    private static final VoxelShape SHAPE = Shapes.or(
        Shapes.box(0f, 0.4999f, 0f, 1f, 0.5001f, 1f),
        Shapes.joinUnoptimized(
            Shapes.box(0f, 0.25f, 0f, 1f, 0.75f, 1f),
            Shapes.box(2f / 16f, 0f, 2f / 16f, 14 / 16f, 1f, 14f / 16f),
            BooleanOp.ONLY_FIRST
        )
    );

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * Insert or remove fluids from both tanks, remove items from all inventories, or insert or remove the filter.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn(level, pos, sieve -> {
            if(hasFilterEntry(state, hitResult.getDirection())) {
                // insert or remove filter
                if(stack.isEmpty()) {
                    // remove filter
                    var output = sieve.filterInventory().extractItem(0, 1, level.isClientSide());
                    if(!output.isEmpty()) {
                        if(!level.isClientSide()) {
                            player.setItemInHand(hand, output);
                            player.gameEvent(GameEvent.BLOCK_CHANGE);
                        }
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                } else {
                    // insert filter
                    var toInsert = stack.copyWithCount(1);
                    var rejected = sieve.filterInventory().insertItem(0, toInsert, true);
                    if(rejected.isEmpty()) {
                        if(!level.isClientSide()) {
                            sieve.filterInventory().insertItem(0, toInsert, false);
                            stack.shrink(1);
                            player.gameEvent(GameEvent.BLOCK_CHANGE);
                        }
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            } else {
                if(stack.isEmpty()) {
                    // remove items from all inventories
                    if(!level.isClientSide()) {
                        var lowerInventory = sieve.lowerInventory();
                        var outputStack = lowerInventory.extractItem(0, 64, true);
                        if(!outputStack.isEmpty() && player.addItem(outputStack)) {
                            lowerInventory.extractItem(0, 64, false);
                        }
                        var upperInventory = sieve.upperInventory();
                        var slots = upperInventory.getSlots();
                        for(var slot = 0; slot < slots; slot++) {
                            var invStack = upperInventory.extractItem(slot, 64, true);
                            if(!invStack.isEmpty() && player.addItem(invStack)) {
                                upperInventory.extractItem(slot, 64, false);
                            }
                        }
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                } else {
                    // try fluid interactions
                    if(tryFillFluid(sieve.lowerTank(), stack, level, player, hand)) {
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                    if(tryFillFluid(sieve.upperTank(), stack, level, player, hand)) {
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                    var emptying = RecipeUtil.findEmptyingResult(stack, level);
                    if(emptying != null && tryDrainFluid(emptying, sieve.upperTank(), stack, level, player, hand)) {
                        return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        });
    }

    private static boolean tryFillFluid(IFluidHandler handler, ItemStack container, Level level, Player player, InteractionHand hand) {
        var supply = handler.drain(1000, IFluidHandler.FluidAction.SIMULATE);
        if(!supply.isEmpty()) {
            var filling = RecipeUtil.findFillingResult(container, level, supply, supply.getAmount());
            if(filling != null && supply.getAmount() >= filling.requiredFluidAmount()) {
                if(!level.isClientSide()) {
                    handler.drain(filling.requiredFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                    if(container.getCount() == 1) {
                        player.setItemInHand(hand, filling.resultStack());
                    } else {
                        container.shrink(1);
                        if(!player.addItem(filling.resultStack())) {
                            player.drop(filling.resultStack(), false);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean tryDrainFluid(RecipeUtil.EmptyingResult emptying, IFluidHandler handler, ItemStack container, Level level, Player player, InteractionHand hand) {
        var supply = emptying.resultFluid();
        var filled = handler.fill(supply, IFluidHandler.FluidAction.SIMULATE);
        if(filled == supply.getAmount()) {
            if(!level.isClientSide()) {
                handler.fill(supply, IFluidHandler.FluidAction.EXECUTE);
                var remaining = emptying.remainingItem();
                if(container.getCount() == 1) {
                    player.setItemInHand(hand, remaining);
                } else {
                    container.shrink(1);
                    if(!player.addItem(remaining)) {
                        player.drop(remaining, false);
                    }
                }
            }
            return true;
        }
        return false;
    }
}
