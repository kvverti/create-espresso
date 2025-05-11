package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import systems.thedawn.espresso.Espresso;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EspressoGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public EspressoGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Espresso.MODID);
    }

    @Override
    protected void start() {
        this.add("coffee_beans/mineshaft_chest",
            new AddTableLootModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build()
            }, EspressoChestLootProvider.CHEST_COFFEE_BEANS));
    }
}
