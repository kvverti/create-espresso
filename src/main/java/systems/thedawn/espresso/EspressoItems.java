package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.item.DrinkBottleItem;
import systems.thedawn.espresso.item.DrinkItem;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;

public class EspressoItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Espresso.MODID);

    public static final DeferredItem<?> COFFEE_CHERRY = ITEMS.registerSimpleBlockItem("coffee_cherry", EspressoBlocks.COFFEE_PLANT);
    public static final DeferredItem<?> COFFEE_PASTE = ITEMS.registerSimpleItem("coffee_paste");
    public static final DeferredItem<?> COFFEE_PIT = ITEMS.registerSimpleItem("coffee_pit");
    public static final DeferredItem<?> COFFEE_BEANS = ITEMS.registerSimpleItem("coffee_beans");
    public static final DeferredItem<?> COFFEE_GROUNDS = ITEMS.registerSimpleItem("coffee_grounds");
    public static final DeferredItem<?> SPENT_COFFEE_GROUNDS = ITEMS.registerSimpleItem("spent_coffee_grounds");
    public static final DeferredItem<?> COFFEE_FILTER = ITEMS.registerSimpleItem("coffee_filter");
    public static final DeferredItem<?> USED_COFFEE_FILTER = ITEMS.registerSimpleItem("used_coffee_filter");
    public static final DeferredItem<?> ICE_CUBES = ITEMS.registerSimpleItem("ice_cubes");
    public static final DeferredItem<?> CRUSHED_ICE = ITEMS.registerSimpleItem("crushed_ice");
    public static final DeferredItem<?> HOT_WATER_BUCKET = ITEMS.register("hot_water_bucket", () -> new BucketItem(
        EspressoFluids.SOURCE_HOT_WATER.value(),
        new Item.Properties().stacksTo(1)
    ));
    public static final DeferredItem<?> HOT_MILK_BOTTLE = ITEMS.registerSimpleItem("hot_milk_bottle");
    public static final DeferredItem<?> DRINK_BOTTLE =
        ITEMS.registerItem("drink_bottle", DrinkBottleItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<?> COFFEE_MUG = ITEMS.registerSimpleBlockItem(EspressoBlocks.COFFEE_MUG);
    public static final DeferredItem<?> TALL_GLASS = ITEMS.registerSimpleBlockItem(EspressoBlocks.TALL_GLASS);
    public static final DeferredItem<DrinkItem> FILLED_COFFEE_MUG =
        ITEMS.registerItem("filled_coffee_mug", props -> new DrinkItem(EspressoBlocks.COFFEE_MUG.value(), COFFEE_MUG.value(), props), new Item.Properties().stacksTo(1));
    public static final DeferredItem<DrinkItem> FILLED_TALL_GLASS =
        ITEMS.registerItem("filled_tall_glass", props -> new DrinkItem(EspressoBlocks.TALL_GLASS.value(), TALL_GLASS.value(), props), new Item.Properties().stacksTo(1));
    public static final DeferredItem<?> STEEPER = ITEMS.registerSimpleBlockItem(EspressoBlocks.STEEPER);
    public static final DeferredItem<?> POUR_OVER_COFFEE_SETUP =
        ITEMS.registerSimpleItem("pour_over_coffee_setup", new Item.Properties().stacksTo(1));
    public static final DeferredItem<?> INCOMPLETE_POUR_OVER_COFFEE_SETUP =
        ITEMS.registerSimpleItem("incomplete_pour_over_coffee_setup", new Item.Properties().stacksTo(1));
    public static final DeferredItem<?> COFFEE_BRICK = ITEMS.registerSimpleItem("coffee_brick");
    public static final DeferredItem<?> COFFEE_BRICKS = ITEMS.registerSimpleBlockItem(EspressoBlocks.COFFEE_BRICKS);
    public static final DeferredItem<?> COFFEE_BRICK_SLAB = ITEMS.registerSimpleBlockItem(EspressoBlocks.COFFEE_BRICK_SLAB);
    public static final DeferredItem<?> COFFEE_BRICK_STAIRS = ITEMS.registerSimpleBlockItem(EspressoBlocks.COFFEE_BRICK_STAIRS);
    public static final DeferredItem<?> SIEVE = ITEMS.registerSimpleBlockItem(EspressoBlocks.SIEVE);
}
