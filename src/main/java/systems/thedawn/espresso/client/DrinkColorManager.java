package systems.thedawn.espresso.client;

import java.io.IOException;
import java.util.Objects;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.block.DrinkBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
        try(var resource = resourceManager.openAsReader(DRINK_COLORS)) {
            this.drinkColors = GSON.fromJson(resource, DrinkColorResource.class);
        } catch(IOException | JsonParseException ex) {
            // reset drink colors
            LogUtils.getLogger().error("Error fetching drink base colors", ex);
            this.drinkColors = new DrinkColorResource();
        }

        this.modifierOverrides.clear();
        var allModifierResources = resourceManager.listResources(MODIFIER_BASE_LOOKUP, loc -> loc.getPath().endsWith(MODIFIER_SUFFIX));
        for(var entry : allModifierResources.entrySet()) {
            try(var reader = entry.getValue().openAsReader()) {
                var colors = GSON.fromJson(reader, DrinkColorResource.class);
                this.modifierOverrides.put(entry.getKey(), colors);
            } catch(IOException | JsonParseException ex) {
                LogUtils.getLogger().error("Error fetching drink modifier colors", ex);
            }
        }
    }
}
