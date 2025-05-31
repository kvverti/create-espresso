package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.AllTags;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoItems;
import systems.thedawn.espresso.EspressoTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
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
                EspressoItems.DRINK_BOTTLE.value(),
                EspressoItems.FILLED_COFFEE_MUG.value(),
                EspressoItems.INCOMPLETE_POUR_OVER_COFFEE_SETUP.value(),
                EspressoItems.POUR_OVER_COFFEE_SETUP.value()
            );
        this.tag(EspressoTags.STEEPER_ENABLED_ITEMS)
            .add(EspressoItems.COFFEE_GROUNDS.value());
        this.tag(EspressoTags.COARSE_FILTERS)
            .add(Items.GOLDEN_PICKAXE);
    }
}
