package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import systems.thedawn.espresso.client.DrinkColorManager;
import systems.thedawn.espresso.client.DrinkColorResource;
import systems.thedawn.espresso.drink.BuiltinEspressoDrinks;
import systems.thedawn.espresso.drink.Drink;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EspressoDrinkColorProvider implements DataProvider {
    private final PackOutput output;
    private final Object2IntOpenHashMap<ResourceLocation> drinkBaseColors = new Object2IntOpenHashMap<>();

    public EspressoDrinkColorProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        // coffee = 492706, latte = f3b273
        final int coffeeColor = 0x492706;
        this.registerColor(BuiltinEspressoDrinks.DIRTY_COLD_BREW, coffeeColor);
        this.registerColor(BuiltinEspressoDrinks.COLD_BREW, coffeeColor);
        this.registerColor(BuiltinEspressoDrinks.POUR_OVER, coffeeColor);
        this.registerColor(BuiltinEspressoDrinks.ESPRESSO, coffeeColor);

        var resource = new DrinkColorResource(this.drinkBaseColors);
        var json = DrinkColorManager.GSON.toJsonTree(resource);
        var assetsRoot = this.output.getOutputFolder();
        var drinkColorLoc = DrinkColorManager.DRINK_COLORS;
        return DataProvider.saveStable(output, json, assetsRoot.resolve("assets/" + drinkColorLoc.getNamespace() + "/" + drinkColorLoc.getPath()));
    }

    private void registerColor(ResourceKey<Drink> drinkKey, int color) {
        this.drinkBaseColors.put(drinkKey.location(), color);
    }

    @Override
    public String getName() {
        return "Espresso drinks provider";
    }
}
