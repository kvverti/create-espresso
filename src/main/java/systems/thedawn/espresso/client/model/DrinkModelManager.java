package systems.thedawn.espresso.client.model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.client.condition.ConditionManager;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

public final class DrinkModelManager {
    public static final DrinkModelManager INSTANCE = new DrinkModelManager();

    private static final String MODEL_BASE_LOOKUP = "drink/models";
    public static final String MODEL_BASE = MODEL_BASE_LOOKUP + "/";
    public static final String MODEL_SUFFIX = ".json";

    private DrinkModelManager() {
    }

    private final Object2ObjectOpenHashMap<Block, MultipartDrinkModel> drinkModels = new Object2ObjectOpenHashMap<>();

    public static Collection<BakedModel> getBlockModels(Block block, DrinkComponent drink, ModelManager manager) {
        var multipart = INSTANCE.drinkModels.get(block);
        if(multipart == null) {
            return List.of();
        }
        return multipart.entries()
            .stream()
            .flatMap(entry -> entry.selectModel(drink).stream())
            .map(modelLocation -> manager.getModel(ModelResourceLocation.standalone(modelLocation)))
            .toList();
    }

    @SubscribeEvent
    public void loadAndRegisterModels(ModelEvent.RegisterAdditional ev) {
        var manager = Minecraft.getInstance().getResourceManager();
        // conditions must be reloaded before drink models
        ConditionManager.INSTANCE.reload(manager);

        this.drinkModels.clear();
        var blocks = EspressoBlockEntityTypes.DRINK.value().getValidBlocks();
        for(var block : blocks) {
            var modelLoc = BuiltInRegistries.BLOCK.getKey(block).withPrefix(MODEL_BASE).withSuffix(MODEL_SUFFIX);
            manager.getResource(modelLoc)
                .flatMap(DrinkModelManager::parse)
                .ifPresent(model -> {
                    submitBakeryOrder(model, ev);
                    this.drinkModels.put(block, model);
                });
        }
        this.drinkModels.trim();
    }

    private static Optional<MultipartDrinkModel> parse(Resource resource) {
        try(var reader = resource.openAsReader()) {
            var json = GsonHelper.parseArray(reader);
            return MultipartDrinkModel.CODEC.parse(JsonOps.INSTANCE, json)
                .result();
        } catch(IOException ex) {
            LogUtils.getLogger().error("Error parsing drink model file from pack: " + resource.sourcePackId(), ex);
            return Optional.empty();
        }
    }

    private static void submitBakeryOrder(MultipartDrinkModel multipart, ModelEvent.RegisterAdditional ev) {
        for(var entry : multipart.entries()) {
            entry.selector().forEachModel(model -> ev.register(ModelResourceLocation.standalone(model)));
        }
    }
}
