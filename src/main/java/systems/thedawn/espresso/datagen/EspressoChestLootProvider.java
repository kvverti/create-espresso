package systems.thedawn.espresso.datagen;

import java.util.function.BiConsumer;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class EspressoChestLootProvider implements LootTableSubProvider {
    public static final ResourceKey<LootTable> CHEST_COFFEE_BEANS =
        ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "chest/coffee_beans"));

    private final HolderLookup.Provider registries;

    public EspressoChestLootProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        // coffee beans in chests
        output.accept(CHEST_COFFEE_BEANS, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(2f))
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 3f)))
                .when(LootItemRandomChanceCondition.randomChance(0.5f))
                .add(LootItem.lootTableItem(EspressoItems.COFFEE_BEANS))));
    }
}
