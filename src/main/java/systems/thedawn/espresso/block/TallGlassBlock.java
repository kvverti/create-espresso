package systems.thedawn.espresso.block;

import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallGlassBlock extends AbstractDrinkBlock {

    public TallGlassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Item getEmptyItem() {
        return EspressoItems.TALL_GLASS.value();
    }

    @Override
    public Item getFilledItem() {
        return EspressoItems.FILLED_TALL_GLASS.value();
    }

    private static final VoxelShape SHAPE = Shapes.box(5f / 16f, 0f, 5f / 16f, 11f / 16f, 10f / 16f, 11f / 16f);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
