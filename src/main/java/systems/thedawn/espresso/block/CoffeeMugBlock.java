package systems.thedawn.espresso.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoffeeMugBlock extends TransparentBlock {
    public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<HumanoidArm> CHIRALITY = EnumProperty.create("chirality", HumanoidArm.class);

    public CoffeeMugBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHIRALITY);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        var facing = state.getValue(FACING);
        return state.setValue(FACING, rotation.rotate(facing));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var playerFacing = context.getHorizontalDirection();
        HumanoidArm chirality;
        if(context.getPlayer() != null) {
            chirality = context.getPlayer().getMainArm();
        } else {
            chirality = HumanoidArm.RIGHT;
        }
        var direction = chirality == HumanoidArm.LEFT ? playerFacing.getCounterClockWise() : playerFacing.getClockWise();
        return this.defaultBlockState()
            .setValue(FACING, direction)
            .setValue(CHIRALITY, chirality);
    }

    private static final VoxelShape SHAPE = Shapes.box(5f / 16f, 0f, 5f / 16f, 11f / 16f, 6f / 16f, 11f / 16f);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
