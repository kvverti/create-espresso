package systems.thedawn.espresso.block;

import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallGlassBlock extends AbstractDrinkBlock {
    public static final Property<Boolean> HAS_MILK = BooleanProperty.create("has_milk");
    public static final Property<Boolean> HAS_BUBBLES = BooleanProperty.create("has_bubbles");

    public TallGlassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_MILK, HAS_BUBBLES);
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
