package systems.thedawn.espresso.datagen;

import java.util.Set;
import java.util.stream.Collectors;

import net.neoforged.neoforge.registries.DeferredHolder;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;

public class EspressoBlockLootProvider extends BlockLootSubProvider {
    public EspressoBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EspressoBlocks.BLOCKS
            .getEntries()
            .stream().map(DeferredHolder::value)
            .filter(b -> !(b instanceof LiquidBlock))
            .collect(Collectors.toList());
    }

    @Override
    protected void generate() {
        this.dropOther(EspressoBlocks.COFFEE_PLANT.value(), EspressoItems.COFFEE_CHERRY);
        this.dropSelf(EspressoBlocks.COFFEE_BRICKS.value());
        this.add(EspressoBlocks.COFFEE_BRICK_SLAB.value(), this.createSlabItemTable(EspressoBlocks.COFFEE_BRICK_SLAB.value()));
        this.dropSelf(EspressoBlocks.COFFEE_BRICK_STAIRS.value());
        this.dropSelf(EspressoBlocks.COFFEE_MUG.value());
    }
}
