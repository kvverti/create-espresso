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
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.client.condition.ConditionManager;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

public final class DrinkModelManager {
    public static final DrinkModelManager INSTANCE = new DrinkModelManager();

    private static final String BLOCK_MODEL_BASE_LOOKUP = "drink/models/block";
    public static final String BLOCK_MODEL_BASE = BLOCK_MODEL_BASE_LOOKUP + "/";
    private static final String ITEM_MODEL_BASE_LOOKUP = "drink/models/item";
    public static final String ITEM_MODEL_BASE = ITEM_MODEL_BASE_LOOKUP + "/";
    public static final String MODEL_SUFFIX = ".json";

    private DrinkModelManager() {
    }

    private final Object2ObjectOpenHashMap<Block, MultipartDrinkModel> drinkBlockModels = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<Block, MultipartDrinkModel> drinkItemModels = new Object2ObjectOpenHashMap<>();

    /**
     * A (non-persistent) timestamp used to determine whether model data stored in blocks/items should be reloaded.
     */
    private long lastLoadTimestamp = 0L;

    public static long lastLoadTimestamp() {
        return INSTANCE.lastLoadTimestamp;
    }

    public static Collection<BakedModel> getBlockModels(Block block, DrinkComponent drink, ModelManager manager) {
        return getBakedModels(drink, manager, INSTANCE.drinkBlockModels.get(block));
    }

    public static Collection<BakedModel> getItemModels(Block block, DrinkComponent drink, ModelManager manager) {
        return getBakedModels(drink, manager, INSTANCE.drinkItemModels.get(block));
    }

    private static List<BakedModel> getBakedModels(DrinkComponent drink, ModelManager manager, @Nullable MultipartDrinkModel multipart) {
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
        this.lastLoadTimestamp++;

        loadModelMap(this.drinkBlockModels, BLOCK_MODEL_BASE, manager, ev);
        loadModelMap(this.drinkItemModels, ITEM_MODEL_BASE, manager, ev);
    }

    private static void loadModelMap(Object2ObjectOpenHashMap<Block, MultipartDrinkModel> models, String prefix, ResourceManager manager, ModelEvent.RegisterAdditional ev) {
        models.clear();
        var blocks = EspressoBlockEntityTypes.DRINK.value().getValidBlocks();
        for(var block : blocks) {
            var modelLoc = BuiltInRegistries.BLOCK.getKey(block).withPrefix(prefix).withSuffix(MODEL_SUFFIX);
            manager.getResource(modelLoc)
                .flatMap(DrinkModelManager::parse)
                .ifPresent(model -> {
                    submitBakeryOrder(model, ev);
                    models.put(block, model);
                });
        }
        models.trim();
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
