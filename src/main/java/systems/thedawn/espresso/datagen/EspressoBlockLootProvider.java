package systems.thedawn.espresso.datagen;

import java.util.Set;
import java.util.stream.Collectors;

import net.neoforged.neoforge.registries.DeferredHolder;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.EspressoItems;
import systems.thedawn.espresso.block.AbstractDrinkBlock;
import systems.thedawn.espresso.block.CoffeePlantBlock;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

public class EspressoBlockLootProvider extends BlockLootSubProvider {
    public EspressoBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EspressoBlocks.BLOCKS
            .getEntries()
            .stream().map(DeferredHolder::value)
            .filter(b -> !(b instanceof LiquidBlock))
            .collect(Collectors.toList());
    }

    @Override
    protected void generate() {
        this.coffeePlantDrops();
        this.drinkHolderDrops(EspressoBlocks.COFFEE_MUG.value());
        this.drinkHolderDrops(EspressoBlocks.TALL_GLASS.value());

        this.dropSelf(EspressoBlocks.COFFEE_BRICKS.value());
        this.add(EspressoBlocks.COFFEE_BRICK_SLAB.value(), this.createSlabItemTable(EspressoBlocks.COFFEE_BRICK_SLAB.value()));
        this.dropSelf(EspressoBlocks.COFFEE_BRICK_STAIRS.value());
        this.dropSelf(EspressoBlocks.STEEPER.value());
        this.dropSelf(EspressoBlocks.SIEVE.value());
    }

    private void coffeePlantDrops() {
        this.dropOther(EspressoBlocks.COFFEE_PLANT.value(), EspressoItems.COFFEE_CHERRY);
        var lootTable = this.createCropDrops(
            EspressoBlocks.GROWN_COFFEE_PLANT.value(),
            EspressoItems.COFFEE_CHERRY.value(),
            EspressoItems.COFFEE_CHERRY.value(),
            LootItemBlockStatePropertyCondition.hasBlockStateProperties(EspressoBlocks.GROWN_COFFEE_PLANT.value())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                    .hasProperty(CoffeePlantBlock.AGE, CoffeePlantBlock.MAX_AGE))
        );
        this.add(EspressoBlocks.GROWN_COFFEE_PLANT.value(), lootTable);
    }

    private void drinkHolderDrops(AbstractDrinkBlock block) {
        var lootTable = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(AbstractDrinkBlock.HAS_DRINK, false)))
                .add(LootItem.lootTableItem(block.getEmptyItem())))
            .withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(AbstractDrinkBlock.HAS_DRINK, true)))
                .add(LootItem.lootTableItem(block.getFilledItem()))
                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                    .include(EspressoDataComponentTypes.DRINK.value())));
        this.add(block, lootTable);
    }
}
