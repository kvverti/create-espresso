package systems.thedawn.espresso.block;

import org.jetbrains.annotations.NotNull;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MugBlock extends AbstractDrinkBlock {
    public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<HumanoidArm> CHIRALITY = EnumProperty.create("chirality", HumanoidArm.class);

    public MugBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, CHIRALITY);
    }

    @Override
    public Item getEmptyItem() {
        return EspressoItems.COFFEE_MUG.value();
    }

    @Override
    public Item getFilledItem() {
        return EspressoItems.FILLED_COFFEE_MUG.value();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        var facing = state.getValue(FACING);
        return state.setValue(FACING, rotation.rotate(facing));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        var facing = state.getValue(FACING);
        if(mirror.mirror(facing) == facing) {
            // keep handle facing the same direction
            return state.cycle(CHIRALITY).setValue(FACING, facing.getOpposite());
        }
        // switch handle to the other side
        return state.cycle(CHIRALITY);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        var playerFacing = context.getHorizontalDirection();
        HumanoidArm chirality;
        if(context.getPlayer() != null) {
            chirality = context.getPlayer().getMainArm();
        } else {
            chirality = HumanoidArm.RIGHT;
        }
        var direction = chirality == HumanoidArm.LEFT ? playerFacing.getCounterClockWise() : playerFacing.getClockWise();
        return super.getStateForPlacement(context)
            .setValue(FACING, direction)
            .setValue(CHIRALITY, chirality);
    }

    private static final VoxelShape SHAPE = Shapes.box(5f / 16f, 0f, 5f / 16f, 11f / 16f, 6f / 16f, 11f / 16f);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
