package systems.thedawn.espresso.datagen;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.data.PackOutput;

public class EspressoItemModelProvider extends ItemModelProvider {
    public EspressoItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Espresso.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(EspressoItems.HOT_WATER_BUCKET.value());
        this.basicItem(EspressoItems.COFFEE_CHERRY.value());
        this.basicItem(EspressoItems.COFFEE_PASTE.value());
        this.basicItem(EspressoItems.COFFEE_PIT.value());
        this.basicItem(EspressoItems.COFFEE_BEANS.value());
        this.basicItem(EspressoItems.COFFEE_GROUNDS.value());
        this.basicItem(EspressoItems.SPENT_COFFEE_GROUNDS.value());
        this.basicItem(EspressoItems.COFFEE_BRICK.value());
        this.simpleBlockItem(EspressoBlocks.COFFEE_BRICKS.value());
        this.simpleBlockItem(EspressoBlocks.COFFEE_BRICK_SLAB.value());
        this.simpleBlockItem(EspressoBlocks.COFFEE_BRICK_STAIRS.value());
        this.basicItem(EspressoItems.COFFEE_MUG.value());
        this.basicItem(EspressoItems.COFFEE_FILTER.value());
        this.basicItem(EspressoItems.USED_COFFEE_FILTER.value());
        this.basicItem(EspressoItems.DRINK_BOTTLE.value());
        this.basicItem(EspressoItems.INCOMPLETE_POUR_OVER_COFFEE_SETUP.value());
        this.basicItem(EspressoItems.POUR_OVER_COFFEE_SETUP.value());
    }
}
