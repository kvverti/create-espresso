package systems.thedawn.espresso.block.sieve;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class SieveBlock extends HorizontalDirectionalBlock implements IBE<SieveBlockEntity>, IWrenchable {
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
        builder.add(FACING);
    }

    @Override
    public Class<SieveBlockEntity> getBlockEntityClass() {
        return SieveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SieveBlockEntity> getBlockEntityType() {
        return EspressoBlockEntityTypes.SIEVE.value();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
