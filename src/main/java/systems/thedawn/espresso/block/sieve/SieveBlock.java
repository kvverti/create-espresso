package systems.thedawn.espresso.block.sieve;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.recipe.FilterCondition;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

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
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch(rotation) {
            case NONE, CLOCKWISE_180 -> state;
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.cycle(AXIS);
        };
    }
}
