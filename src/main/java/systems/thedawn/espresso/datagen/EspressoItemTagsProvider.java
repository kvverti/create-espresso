package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.AllTags;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

public class EspressoItemTagsProvider extends ItemTagsProvider {

    public EspressoItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, Espresso.MODID, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag)
            .add(
                EspressoItems.COFFEE_MUG.value(),
                EspressoItems.MIXED_COFFEE_BOTTLE.value(),
                EspressoItems.INCOMPLETE_POUR_OVER_COFFEE_SETUP.value(),
                EspressoItems.POUR_OVER_COFFEE_SETUP.value(),
                EspressoItems.POUR_OVER_COFFEE_BOTTLE.value()
            );
    }
}
