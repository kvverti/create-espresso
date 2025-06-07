package systems.thedawn.espresso.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.JsonOps;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.client.model.*;
import systems.thedawn.espresso.client.condition.BuiltinConditions;
import systems.thedawn.espresso.client.condition.DeferredCondition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class EspressoDrinkModelProvider implements DataProvider {
    private static final DeferredCondition ALWAYS_TRUE = DeferredCondition.indirect(BuiltinConditions.HAS_DRINK);

    private final PackOutput packOutput;
    private final List<ModelEntry> models = new ArrayList<>();

    public EspressoDrinkModelProvider(PackOutput output) {
        this.packOutput = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.registerModels();
        return CompletableFuture.allOf(
            this.models
                .stream()
                .map(entry -> this.resourceFile(output, entry))
                .toArray(CompletableFuture<?>[]::new)
        );
    }

    private CompletableFuture<?> resourceFile(CachedOutput output, ModelEntry entry) {
        var fileLocation = entry.location
            .withPrefix(DrinkModelManager.MODEL_BASE)
            .withSuffix(DrinkModelManager.MODEL_SUFFIX);
        var json = MultipartDrinkModel.CODEC.encodeStart(JsonOps.INSTANCE, entry.multipart).getOrThrow();
        var assetsRoot = this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK);
        return DataProvider.saveStable(output, json, assetsRoot.resolve(fileLocation.getNamespace() + "/" + fileLocation.getPath()));
    }

    private void registerModels() {
        this.registerBlock(EspressoBlocks.COFFEE_MUG.value(), new MultipartDrinkModel(List.of(
            new MultipartEntry(ALWAYS_TRUE, ModelSelector.single(modLoc("block/mug_drink")))
        )));
        this.registerBlock(EspressoBlocks.TALL_GLASS.value(), new MultipartDrinkModel(List.of(
            new MultipartEntry(ALWAYS_TRUE, ModelSelector.single(modLoc("block/tall_glass_drink"))),
            new MultipartEntry(
                DeferredCondition.indirect(BuiltinConditions.HAS_BUBBLES),
                ModelSelector.single(modLoc("block/tall_glass_bubbles"))
            ),
            new MultipartEntry(
                DeferredCondition.indirect(BuiltinConditions.HAS_ICE),
                ModelSelector.single(modLoc("block/tall_glass_ice"))
            ),
            new MultipartEntry(
                DeferredCondition.indirect(BuiltinConditions.HAS_MILK),
                ModelSelector.alternatives(
                    new ConditionModelPair(
                        DeferredCondition.indirect(BuiltinConditions.IS_COFFEE),
                        modLoc("block/tall_glass_latte")
                    ),
                    new ConditionModelPair(ALWAYS_TRUE, modLoc("block/tall_glass_milk"))
                )
            )
        )));
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Espresso.MODID, path);
    }

    private void registerBlock(Block block, MultipartDrinkModel multipart) {
        var location = BuiltInRegistries.BLOCK.getKey(block);
        this.models.add(new ModelEntry(location, multipart));
    }

    @Override
    public String getName() {
        return "Espresso drink model provider";
    }

    private record ModelEntry(ResourceLocation location, MultipartDrinkModel multipart) {
    }
}
