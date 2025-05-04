package systems.thedawn.espresso.datagen;

import net.neoforged.neoforge.common.data.LanguageProvider;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.data.PackOutput;

public class EspressoTranslationProvider extends LanguageProvider {

    public EspressoTranslationProvider(PackOutput output) {
        super(output, Espresso.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // tab key
        this.add("itemGroup.create_espresso", "Create: Espresso");
        // blocks
        this.addBlock(EspressoBlocks.HOT_WATER, "Hot Water");
        //this.addBlock(EspressoBlocks.COFFEE_PLANT, "Coffee Plant");
        this.addBlock(EspressoBlocks.COFFEE_BRICKS, "Coffee Bricks");
        this.addBlock(EspressoBlocks.COFFEE_BRICK_SLAB, "Coffee Brick Slab");
        this.addBlock(EspressoBlocks.COFFEE_BRICK_STAIRS, "Coffee Brick Stairs");
        this.addBlock(EspressoBlocks.COFFEE_MUG, "Coffee Mug");
        // Items
        this.addItem(EspressoItems.COFFEE_CHERRY, "Coffee Cherry");
        this.addItem(EspressoItems.COFFEE_PASTE, "Coffee Paste");
        this.addItem(EspressoItems.COFFEE_PIT, "Coffee Pit");
        this.addItem(EspressoItems.COFFEE_BEANS, "Coffee Beans");
        this.addItem(EspressoItems.COFFEE_GROUNDS, "Coffee Grounds");
        this.addItem(EspressoItems.SPENT_COFFEE_GROUNDS, "Spent Coffee Grounds");
        this.addItem(EspressoItems.COFFEE_FILTER, "Coffee Filter");
        this.addItem(EspressoItems.USED_COFFEE_FILTER, "Used Coffee Filter");
        this.addItem(EspressoItems.COFFEE_BRICK, "Coffee Brick");
        this.addItem(EspressoItems.HOT_WATER_BUCKET, "Hot Water Bucket");
        this.addItem(EspressoItems.INCOMPLETE_POUR_OVER_COFFEE_SETUP, "Incomplete Pour Over Coffee Setup");
        this.addItem(EspressoItems.POUR_OVER_COFFEE_SETUP, "Pour Over Coffee Setup");
        // fluids
        this.add("fluid_type.create_espresso.hot_water", "Hot Water");
        this.add("fluid_type.create_espresso.espresso", "Coffee");
    }
}
