package systems.thedawn.espresso;

import com.simibubi.create.AllCreativeModeTabs;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.drink.BuiltinEspressoDrinks;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkComponent;

public final class EspressoCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Espresso.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ESPRESSO_TAB = CREATIVE_MODE_TABS.register("espresso", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.create_espresso"))
        .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
        .icon(EspressoItems.COFFEE_BEANS::toStack)
        .displayItems((parameters, output) -> {
            // blocks
            output.accept(EspressoItems.COFFEE_BRICKS);
            output.accept(EspressoItems.COFFEE_BRICK_SLAB);
            output.accept(EspressoItems.COFFEE_BRICK_STAIRS);
            output.accept(EspressoItems.COFFEE_MUG);
            output.accept(EspressoItems.TALL_GLASS);
            // items
            output.accept(EspressoItems.COFFEE_CHERRY);
            output.accept(EspressoItems.COFFEE_PASTE);
            output.accept(EspressoItems.COFFEE_PIT);
            output.accept(EspressoItems.COFFEE_BEANS);
            output.accept(EspressoItems.COFFEE_GROUNDS);
            output.accept(EspressoItems.SPENT_COFFEE_GROUNDS);
            output.accept(EspressoItems.COFFEE_FILTER);
            output.accept(EspressoItems.ICE_CUBES);
            output.accept(EspressoItems.CRUSHED_ICE);
            output.accept(EspressoItems.COFFEE_BRICK);
            output.accept(EspressoItems.HOT_WATER_BUCKET);
            output.accept(EspressoItems.HOT_MILK_BOTTLE);
            output.accept(EspressoItems.STEEPER);
            output.accept(EspressoItems.SIEVE);
        }).build());
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> ESPRESSO_DRINKS = CREATIVE_MODE_TABS.register("espresso_drinks", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.create_espresso_drinks"))
        .withTabsBefore(ESPRESSO_TAB.getKey())
        .icon(EspressoItems.DRINK_BOTTLE::toStack)
        .displayItems((parameters, output) -> {
            // drink bottles
            var registries = parameters.holders();
            output.accept(drinkBottle(BuiltinEspressoDrinks.DIRTY_COLD_BREW, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.COLD_BREW, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.POUR_OVER, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.ESPRESSO, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.COFFEE_TEA, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.GREEN_TEA, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.BLACK_TEA, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.HERBAL_TEA, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.APPLE_JUICE, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.FRUIT_PUNCH, registries));
            // drink 
            output.accept(drinkMug(BuiltinEspressoDrinks.COLD_BREW, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.POUR_OVER, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.ESPRESSO, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.COFFEE_TEA, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.GREEN_TEA, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.BLACK_TEA, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.HERBAL_TEA, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.APPLE_JUICE, registries));
            output.accept(drinkMug(BuiltinEspressoDrinks.FRUIT_PUNCH, registries));
            // tall 
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.COLD_BREW, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.POUR_OVER, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.ESPRESSO, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.COFFEE_TEA, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.GREEN_TEA, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.BLACK_TEA, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.HERBAL_TEA, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.APPLE_JUICE, registries));
            output.accept(tallDrinkGlass(BuiltinEspressoDrinks.FRUIT_PUNCH, registries));
        })
        .build()
    );

    private static ItemStack drinkBottle(ResourceKey<Drink> key, HolderLookup.Provider registries) {
        var component = registries.holderOrThrow(key);
        var stack = new ItemStack(EspressoItems.DRINK_BOTTLE.value());
        stack.set(EspressoDataComponentTypes.DRINK_BASE, component);
        return stack;
    }

    private static ItemStack drinkMug(ResourceKey<Drink> key, HolderLookup.Provider registries) {
        return drinkHolder(EspressoItems.FILLED_COFFEE_MUG.toStack(), key, registries);
    }

    private static ItemStack tallDrinkGlass(ResourceKey<Drink> key, HolderLookup.Provider registries) {
        return drinkHolder(EspressoItems.FILLED_TALL_GLASS.toStack(), key, registries);
    }

    private static ItemStack drinkHolder(ItemStack stack, ResourceKey<Drink> key, HolderLookup.Provider registries) {
        var drinkBase = registries.holderOrThrow(key);
        var component = DrinkComponent.initial(drinkBase);
        stack.set(EspressoDataComponentTypes.DRINK, component);
        return stack;
    }
}
