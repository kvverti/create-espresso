package systems.thedawn.espresso.block;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.CommonHooks;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class CoffeePlantBlock extends BushBlock implements BonemealableBlock {
    public static final Property<Integer> AGE = BlockStateProperties.AGE_5;
    private static final int MAX_AGE = 5;

    public CoffeePlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    private static BlockState incrementAge(BlockState state) {
        var age = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        return state.setValue(AGE, age);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // harvest coffee cherries
        if(state.getValue(AGE) == MAX_AGE) {
            var amount = level.random.nextInt(1, 4);
            Block.popResource(level, pos, new ItemStack(EspressoItems.COFFEE_CHERRY.value(), amount));
            level.setBlock(pos, state.setValue(AGE, MAX_AGE - 1), 2);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // grow by one age increment
        if(state.getValue(AGE) == MAX_AGE) {
            return;
        }
        var sufficientLight = level.getMaxLocalRawBrightness(pos) >= 10;
        if(CommonHooks.canCropGrow(level, pos, state, sufficientLight)) {
            var newState = incrementAge(state);
            level.setBlock(pos, newState, 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
    }

    private static final MapCodec<CoffeePlantBlock> CODEC = BlockBehaviour.simpleCodec(CoffeePlantBlock::new);

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, incrementAge(state), 2);
    }
}
