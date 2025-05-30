package systems.thedawn.espresso.block.sieve;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.recipe.FilterCondition;
import systems.thedawn.espresso.util.ItemHandlerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
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
                var slot = level.getRandom().nextInt(possibleItems.size());
                var particle = new ItemParticleOption(ParticleTypes.ITEM, possibleItems.get(slot));
                ParticleUtils.spawnParticles(level, pos, 1, 0.5, 0.25, true, particle);
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
}
