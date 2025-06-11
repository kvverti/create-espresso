package systems.thedawn.espresso.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Streams;
import com.mojang.serialization.JsonOps;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.client.condition.BuiltinConditions;
import systems.thedawn.espresso.client.condition.ConditionHolder;
import systems.thedawn.espresso.client.model.*;
import systems.thedawn.espresso.item.DrinkItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class EspressoDrinkModelProvider implements DataProvider {
    private static final ConditionHolder ALWAYS_TRUE = ConditionHolder.indirect(BuiltinConditions.HAS_DRINK);

    private final PackOutput packOutput;
    private final List<ModelEntry> blockModels = new ArrayList<>();
    private final List<ModelEntry> itemModels = new ArrayList<>();

    public EspressoDrinkModelProvider(PackOutput output) {
        this.packOutput = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.registerModels();
        return CompletableFuture.allOf(Streams.concat(
            this.blockModels
                .stream()
                .map(entry -> this.resourceFile(DrinkModelManager.BLOCK_MODEL_BASE, output, entry)),
            this.itemModels
                .stream()
                .map(entry -> this.resourceFile(DrinkModelManager.ITEM_MODEL_BASE, output, entry))
        ).toArray(CompletableFuture<?>[]::new));
    }

    private CompletableFuture<?> resourceFile(String prefix, CachedOutput output, ModelEntry entry) {
        var fileLocation = entry.location
            .withPrefix(prefix)
            .withSuffix(DrinkModelManager.MODEL_SUFFIX);
        var json = MultipartDrinkModel.CODEC.encodeStart(JsonOps.INSTANCE, entry.multipart).getOrThrow();
        var assetsRoot = this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK);
        return DataProvider.saveStable(output, json, assetsRoot.resolve(fileLocation.getNamespace() + "/" + fileLocation.getPath()));
    }

    private void registerModels() {
        // mug
        this.registerBlock(EspressoBlocks.COFFEE_MUG.value(), new MultipartDrinkModel(List.of(
            new MultipartEntry(
                ConditionHolder.indirect(BuiltinConditions.IS_COFFEE),
                ModelSelector.single(Espresso.modLoc("block/coffee_mug_coffee"))
            )
        )));
        // tall glass
        this.registerBlock(EspressoBlocks.TALL_GLASS.value(), new MultipartDrinkModel(List.of(
            new MultipartEntry(
                ConditionHolder.indirect(BuiltinConditions.IS_COFFEE),
                ModelSelector.single(Espresso.modLoc("block/tall_glass_coffee"))
            ),
            new MultipartEntry(
                ConditionHolder.indirect(BuiltinConditions.HAS_BUBBLES),
                ModelSelector.single(Espresso.modLoc("block/tall_glass_bubbles"))
            ),
            new MultipartEntry(
                ConditionHolder.indirect(BuiltinConditions.HAS_ICE),
                ModelSelector.single(Espresso.modLoc("block/tall_glass_ice"))
            ),
            new MultipartEntry(
                ConditionHolder.indirect(BuiltinConditions.HAS_MILK),
                ModelSelector.alternatives(
                    new ConditionModelPair(
                        ConditionHolder.indirect(BuiltinConditions.IS_COFFEE),
                        Espresso.modLoc("block/tall_glass_latte")
                    ),
                    new ConditionModelPair(ALWAYS_TRUE, Espresso.modLoc("block/tall_glass_milk"))
                )
            )
        )));
    }

    private void registerBlock(Block block, MultipartDrinkModel multipart) {
        var location = BuiltInRegistries.BLOCK.getKey(block);
        this.blockModels.add(new ModelEntry(location, multipart));
    }

    private void registerItem(DrinkItem item, MultipartDrinkModel multipart) {
        var location = BuiltInRegistries.BLOCK.getKey(item.getBlock());
        this.itemModels.add(new ModelEntry(location, multipart));
    }

    @Override
    public String getName() {
        return "Espresso drink model provider";
    }

    private record ModelEntry(ResourceLocation location, MultipartDrinkModel multipart) {
    }
}
