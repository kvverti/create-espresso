package systems.thedawn.espresso.datagen;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class EspressoItemModelProvider extends ItemModelProvider {
    public EspressoItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Espresso.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.drinkMug();
        this.tallGlass();

        this.basicItem(EspressoItems.HOT_WATER_BUCKET.value());
        this.basicItem(EspressoItems.HOT_MILK_BOTTLE.value());
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
        this.basicItem(EspressoItems.ICE_CUBES.value());
        this.basicItem(EspressoItems.CRUSHED_ICE.value());
        this.simpleBlockItem(EspressoBlocks.STEEPER.value());
        this.getBuilder(EspressoItems.SIEVE.getRegisteredName())
            .parent(this.getExistingFile(this.modLoc("block/sieve_none")));
        this.basicItem(EspressoItems.TALL_GLASS.value());
    }

    private void dynamic(Holder<Item> item) {
        this.getBuilder(item.getRegisteredName())
            .parent(new ModelFile.ExistingModelFile(Espresso.modLoc("item/dynamic_drink"), this.existingFileHelper));
    }

    private void drinkMug() {
        this.getBuilder(EspressoItems.FILLED_COFFEE_MUG.getRegisteredName())
            .parent(new ModelFile.ExistingModelFile(ResourceLocation.withDefaultNamespace("item/generated"), this.existingFileHelper))
            .texture("layer0", Espresso.modLoc("item/drink_mug_overlay"))
            .texture("layer1", Espresso.modLoc("item/drink_mug_drink"));
    }

    private void tallGlass() {
        this.dynamic(EspressoItems.FILLED_TALL_GLASS);
        this.getBuilder("tall_glass_drink")
            .parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("builtin/generated")))
            .texture("layer0", Espresso.modLoc("item/tall_glass"))
            .texture("layer1", Espresso.modLoc("item/tall_glass_drink"));
        this.getBuilder("tall_glass_latte")
            .parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("builtin/generated")))
            .texture("layer0", Espresso.modLoc("item/tall_glass_latte"));
    }
}
