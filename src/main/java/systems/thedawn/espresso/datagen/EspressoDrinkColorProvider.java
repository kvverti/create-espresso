package systems.thedawn.espresso.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import systems.thedawn.espresso.client.DrinkColorManager;
import systems.thedawn.espresso.client.DrinkColorResource;
import systems.thedawn.espresso.drink.BuiltinDrinkModifiers;
import systems.thedawn.espresso.drink.BuiltinEspressoDrinks;
import systems.thedawn.espresso.drink.Drink;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EspressoDrinkColorProvider implements DataProvider {
    private final PackOutput packOutput;
    private final Object2IntOpenHashMap<ResourceLocation> drinkBaseColors = new Object2IntOpenHashMap<>();
    private final List<ModifierOverride> modifierOverrides = new ArrayList<>();

    public EspressoDrinkColorProvider(PackOutput output) {
        this.packOutput = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.registerColors();

        var futures = new CompletableFuture<?>[1 + this.modifierOverrides.size()];
        var baseResource = new DrinkColorResource(this.drinkBaseColors);
        var drinkColorLoc = DrinkColorManager.DRINK_COLORS;
        int idx = 0;
        futures[idx++] = this.colorResourceFile(output, drinkColorLoc, baseResource);

        for(var modifierOverride : this.modifierOverrides) {
            var fileLocation = modifierOverride.modifier
                .withPrefix(DrinkColorManager.MODIFIER_BASE)
                .withSuffix(DrinkColorManager.MODIFIER_SUFFIX);
            var resource = new DrinkColorResource(modifierOverride.colors);
            futures[idx++] = this.colorResourceFile(output, fileLocation, resource);
        }

        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> colorResourceFile(CachedOutput output, ResourceLocation location, DrinkColorResource resource) {
        var json = DrinkColorManager.GSON.toJsonTree(resource);
        var assetsRoot = this.packOutput.getOutputFolder();
        return DataProvider.saveStable(output, json, assetsRoot.resolve("assets/" + location.getNamespace() + "/" + location.getPath()));
    }

    private void registerColors() {
        final int coffeeColor = 0x492706;
        this.registerColor(BuiltinEspressoDrinks.DIRTY_COLD_BREW, coffeeColor);
        this.registerColor(BuiltinEspressoDrinks.COLD_BREW, coffeeColor);
        this.registerColor(BuiltinEspressoDrinks.POUR_OVER, coffeeColor);
        this.registerColor(BuiltinEspressoDrinks.ESPRESSO, coffeeColor);

        var latteColor = 0xe7ae6b;
        var milkOverrides = new Object2IntOpenHashMap<ResourceLocation>();
        milkOverrides.put(BuiltinEspressoDrinks.DIRTY_COLD_BREW.location(), latteColor);
        milkOverrides.put(BuiltinEspressoDrinks.COLD_BREW.location(), latteColor);
        milkOverrides.put(BuiltinEspressoDrinks.POUR_OVER.location(), latteColor);
        milkOverrides.put(BuiltinEspressoDrinks.ESPRESSO.location(), latteColor);
        this.modifierOverrides.add(new ModifierOverride(BuiltinDrinkModifiers.MILK.location(), milkOverrides));
    }

    private void registerColor(ResourceKey<Drink> drinkKey, int color) {
        this.drinkBaseColors.put(drinkKey.location(), color);
    }

    @Override
    public String getName() {
        return "Espresso drinks provider";
    }

    private record ModifierOverride(ResourceLocation modifier, Object2IntOpenHashMap<ResourceLocation> colors) {
    }
}
