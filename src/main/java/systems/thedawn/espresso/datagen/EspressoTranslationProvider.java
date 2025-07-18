package systems.thedawn.espresso.datagen;

import net.neoforged.neoforge.common.data.LanguageProvider;
import systems.thedawn.espresso.*;
import systems.thedawn.espresso.drink.*;
import systems.thedawn.espresso.item.DrinkUtil;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;

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
        this.addBlock(EspressoBlocks.COFFEE_MUG, "Mug");
        this.addBlock(EspressoBlocks.TALL_GLASS, "Tall Glass");
        this.addBlock(EspressoBlocks.STEEPER, "Steeper");
        this.addBlock(EspressoBlocks.SIEVE, "Sieve");
        // Items
        this.addItem(EspressoItems.COFFEE_CHERRY, "Coffee Cherry");
        this.addItem(EspressoItems.COFFEE_PASTE, "Coffee Paste");
        this.addItem(EspressoItems.COFFEE_PIT, "Coffee Pit");
        this.addItem(EspressoItems.COFFEE_BEANS, "Coffee Beans");
        this.addItem(EspressoItems.COFFEE_GROUNDS, "Coffee Grounds");
        this.addItem(EspressoItems.SPENT_COFFEE_GROUNDS, "Spent Coffee Grounds");
        this.addItem(EspressoItems.COFFEE_FILTER, "Coffee Filter");
        this.addItem(EspressoItems.COFFEE_BRICK, "Coffee Brick");
        this.addItem(EspressoItems.HOT_WATER_BUCKET, "Hot Water Bucket");
        this.addItem(EspressoItems.HOT_MILK_BOTTLE, "Hot Milk Bottle");
        this.addItem(EspressoItems.ICE_CUBES, "Ice Cubes");
        this.addItem(EspressoItems.CRUSHED_ICE, "Crushed Ice");
        this.addBottle(Drink.Type.COFFEE, "Coffee Bottle");
        this.addBottle(Drink.Type.TEA, "Tea Bottle");
        this.addMug(Drink.Type.NONE, "Mug");
        this.addMug(Drink.Type.COFFEE, "Coffee Mug");
        this.addMug(Drink.Type.TEA, "Tea Mug");
        this.addTallGlass(Drink.Type.NONE, "Tall Glass");
        this.addTallGlass(Drink.Type.COFFEE, "Tall Coffee Glass");
        this.addTallGlass(Drink.Type.TEA, "Tall Tea Glass");
        // fluids
        this.add("fluid_type.create_espresso.hot_water", "Hot Water");
        this.add("fluid_type.create_espresso.espresso", "Coffee");
        this.add("fluid_type.create_espresso.hot_milk", "Hot Milk");
        // drinks
        this.addDrink(BuiltinEspressoDrinks.EMPTY, "Uncraftable");
        this.addDrink(BuiltinEspressoDrinks.DIRTY_COLD_BREW, "Dirty Cold Brew");
        this.addDrink(BuiltinEspressoDrinks.COLD_BREW, "Cold Brew");
        this.addDrink(BuiltinEspressoDrinks.POUR_OVER, "Pour Over");
        this.addDrink(BuiltinEspressoDrinks.ESPRESSO, "Espresso");
        // levels
        this.addDrinkLevel(DrinkComponent.BaseLevel.SINGLE, "Single");
        this.addDrinkLevel(DrinkComponent.BaseLevel.DOUBLE, "Double");
        this.addDrinkLevel(DrinkComponent.BaseLevel.TRIPLE, "Triple");
        // modifiers
        this.addModifier(BuiltinDrinkModifiers.ICE, "Iced");
        this.addModifier(BuiltinDrinkModifiers.MILK, "Milk");
        this.addModifier(BuiltinDrinkModifiers.BUBBLES, "Bubbles");
        this.addModifier(BuiltinDrinkModifiers.CHOCOLATE, "Chocolate");
        // jei
        this.add("create_espresso.jei.modification.title", "Drink Modification");
    }

    private void addBottle(Drink.Type type, String name) {
        this.add(DrinkUtil.getDrinkDescriptionId(EspressoItems.DRINK_BOTTLE.value(), type), name);
    }

    private void addMug(Drink.Type type, String name) {
        this.add(DrinkUtil.getDrinkDescriptionId(EspressoItems.FILLED_COFFEE_MUG.value(), type), name);
    }

    private void addTallGlass(Drink.Type type, String name) {
        this.add(DrinkUtil.getDrinkDescriptionId(EspressoItems.FILLED_TALL_GLASS.value(), type), name);
    }

    private void addDrink(ResourceKey<Drink> key, String name) {
        this.add(Drink.getDescriptionId(key), name);
    }

    private void addDrinkLevel(DrinkComponent.BaseLevel level, String name) {
        this.add(level.getDescriptionId(), name);
    }

    private void addModifier(ResourceKey<DrinkModifier> key, String name) {
        this.add(DrinkModifier.getDescriptionId(key), name);
    }
}
