package systems.thedawn.espresso.block;

import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class FilledDrinkBlock extends DrinkBaseBlock implements EntityBlock {
    public FilledDrinkBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrinkBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var stack = super.getCloneItemStack(state, target, level, pos, player);
        level.getBlockEntity(pos, EspressoBlockEntityTypes.DRINK.value())
            .ifPresent(be -> stack.applyComponents(be.collectComponents()));
        return stack;
    }
}
