package systems.thedawn.espresso.client;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.block.DrinkBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class DrinkColorManager implements ResourceManagerReloadListener {
    public static final DrinkColorManager INSTANCE = new DrinkColorManager();

    public static final ResourceLocation DRINK_COLORS = ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "color/drinks.json");
    private static final String MODIFIER_BASE_LOOKUP = "color/modifier";
    public static final String MODIFIER_BASE = MODIFIER_BASE_LOOKUP + "/";
    public static final String MODIFIER_SUFFIX = ".json";

    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(DrinkColorResource.class, new DrinkColorResource.Serializer())
        .create();

    private DrinkColorManager() {
    }

    private DrinkColorResource drinkColors = new DrinkColorResource();
    private final Object2ObjectOpenHashMap<ResourceLocation, DrinkColorResource> modifierOverrides = new Object2ObjectOpenHashMap<>();

    private int getBaseColor(ResourceLocation drinkLoc) {
        return this.drinkColors.lookup(drinkLoc);
    }

    public static int getColor(BlockState ignored, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if(tintIndex == 0) {
            if(level != null && pos != null) {
                var drink = level.getBlockEntity(pos, EspressoBlockEntityTypes.DRINK.value())
                    .map(DrinkBlockEntity::drink)
                    .orElse(null);
                if(drink != null) {
                    var baseLoc = Objects.requireNonNull(drink.base().getKey()).location();
                    var color = DrinkColorManager.INSTANCE.getBaseColor(baseLoc);
                    for(var modifier : drink.modifiers()) {
                        var modifierLoc = Objects.requireNonNull(modifier.getKey())
                            .location()
                            .withPrefix(MODIFIER_BASE)
                            .withSuffix(MODIFIER_SUFFIX);
                        var modifierColors = DrinkColorManager.INSTANCE.modifierOverrides.get(modifierLoc);
                        if(modifierColors != null) {
                            var overrideColor = modifierColors.lookup(baseLoc);
                            if(overrideColor != -1) {
                                color = overrideColor;
                            }
                        }
                    }
                    return color;
                }
            }
        }
        return -1;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.drinkColors = parseStack(resourceManager.getResourceStack(DRINK_COLORS));

        this.modifierOverrides.clear();
        var allModifierResources = resourceManager.listResourceStacks(MODIFIER_BASE_LOOKUP, loc -> loc.getPath().endsWith(MODIFIER_SUFFIX));
        for(var entry : allModifierResources.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                var colors = parseStack(entry.getValue());
                this.modifierOverrides.put(entry.getKey(), colors);
            }
        }
    }

    private static DrinkColorResource parseStack(List<Resource> resources) {
        return resources.stream()
            .map(DrinkColorManager::parse)
            .flatMap(Optional::stream)
            .collect(toSingleResource());
    }

    private static Optional<DrinkColorResource> parse(Resource resource) {
        try(var reader = resource.openAsReader()) {
            return Optional.of(GSON.fromJson(reader, DrinkColorResource.class));
        } catch(IOException | JsonParseException ex) {
            var packId = resource.sourcePackId();
            LogUtils.getLogger().error("Error fetching drink color file from pack: " + packId, ex);
            return Optional.empty();
        }
    }

    private static Collector<DrinkColorResource, ?, DrinkColorResource> toSingleResource() {
        return Collector.of(
            DrinkColorResource::new,
            DrinkColorResource::combine,
            DrinkColorResource::combine,
            Collector.Characteristics.UNORDERED);
    }
}
