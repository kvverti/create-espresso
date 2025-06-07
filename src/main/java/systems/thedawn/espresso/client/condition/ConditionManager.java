package systems.thedawn.espresso.client.condition;

import java.io.IOException;
import java.util.Optional;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoConditionTemplates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;

public final class ConditionManager {
    public static final ConditionManager INSTANCE = new ConditionManager();

    private static final String CONDITION_BASE_LOOKUP = "drink/conditions";
    public static final String CONDITION_BASE = CONDITION_BASE_LOOKUP + "/";
    public static final String CONDITION_SUFFIX = ".json";

    private final Object2ObjectOpenHashMap<ResourceLocation, Condition<?>> conditions = new Object2ObjectOpenHashMap<>();

    private ConditionManager() {
        this.addBuiltinConditions();
    }

    private void addBuiltinConditions() {
        this.conditions.put(BuiltinConditions.HAS_DRINK, new Condition<>(EspressoConditionTemplates.TRIVIAL.value(), Unit.INSTANCE));
    }

    public static @Nullable Condition<?> getCondition(ResourceLocation key) {
        var location = key.withPrefix(CONDITION_BASE).withSuffix(CONDITION_SUFFIX);
        return INSTANCE.conditions.get(location);
    }

    /**
     * Reload conditions from resource packs.
     *
     * @param manager the resource manager
     */
    public void reload(ResourceManager manager) {
        this.conditions.clear();
        this.addBuiltinConditions();

        var resources = manager.listResources(CONDITION_BASE_LOOKUP, loc -> loc.getPath().endsWith(CONDITION_SUFFIX));
        for(var entry : resources.entrySet()) {
            var location = entry.getKey();
            parse(entry.getValue())
                .ifPresent(condition -> this.conditions.put(location, condition));
        }

        this.conditions.trim();
    }

    private Optional<Condition<?>> parse(Resource resource) {
        try(var reader = resource.openAsReader()) {
            var json = GsonHelper.parse(reader);
            return Condition.DIRECT_CODEC.parse(JsonOps.INSTANCE, json)
                .result();
        } catch(IOException ex) {
            LogUtils.getLogger().error("Error parsing condition from pack: " + resource.sourcePackId(), ex);
            return Optional.empty();
        }
    }
}
