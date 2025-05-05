package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.AllTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

public class EspressoBlockTagsProvider extends BlockTagsProvider {
    public EspressoBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Espresso.MODID, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .replace(false)
            .add(
                EspressoBlocks.COFFEE_BRICKS.value(),
                EspressoBlocks.COFFEE_BRICK_SLAB.value(),
                EspressoBlocks.COFFEE_BRICK_STAIRS.value()
            );
        this.tag(AllTags.AllBlockTags.NON_HARVESTABLE.tag)
            .replace(false)
            .add(EspressoBlocks.COFFEE_PLANT.value());
    }
}
