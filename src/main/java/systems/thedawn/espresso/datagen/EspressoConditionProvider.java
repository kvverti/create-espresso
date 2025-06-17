package systems.thedawn.espresso.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.JsonOps;
import systems.thedawn.espresso.EspressoConditionTemplates;
import systems.thedawn.espresso.client.condition.BuiltinConditions;
import systems.thedawn.espresso.client.condition.Condition;
import systems.thedawn.espresso.client.condition.ConditionHolder;
import systems.thedawn.espresso.client.condition.ConditionManager;
import systems.thedawn.espresso.drink.BuiltinDrinkModifiers;
import systems.thedawn.espresso.drink.Drink;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class EspressoConditionProvider implements DataProvider {
    private final PackOutput packOutput;
    private final List<Entry> entries = new ArrayList<>();

    public EspressoConditionProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.registerConditions();
        return CompletableFuture.allOf(
            this.entries
                .stream()
                .map(entry -> this.resourceFile(output, entry))
                .toArray(CompletableFuture<?>[]::new)
        );
    }

    private CompletableFuture<?> resourceFile(CachedOutput output, Entry entry) {
        var fileLocation = entry.location.withPrefix(ConditionManager.CONDITION_BASE).withSuffix(ConditionManager.CONDITION_SUFFIX);
        var json = Condition.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, entry.condition).getOrThrow();
        var assetsRoot = this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK);
        return DataProvider.saveStable(output, json, assetsRoot.resolve(fileLocation.getNamespace() + "/" + fileLocation.getPath()));
    }

    private void registerConditions() {
        this.register(BuiltinConditions.IMPOSSIBLE, new Condition<>(EspressoConditionTemplates.TRIVIAL.value(), false));
        this.register(BuiltinConditions.HAS_DRINK, new Condition<>(EspressoConditionTemplates.TRIVIAL.value(), true));
        this.register(BuiltinConditions.HAS_MILK, new Condition<>(EspressoConditionTemplates.MODIFIER.value(), List.of(BuiltinDrinkModifiers.MILK)));
        this.register(BuiltinConditions.HAS_BUBBLES, new Condition<>(EspressoConditionTemplates.MODIFIER.value(), List.of(BuiltinDrinkModifiers.BUBBLES)));
        this.register(BuiltinConditions.HAS_ICE, new Condition<>(EspressoConditionTemplates.MODIFIER.value(), List.of(BuiltinDrinkModifiers.ICE)));
        this.register(BuiltinConditions.IS_COFFEE, new Condition<>(EspressoConditionTemplates.DRINK_TYPE.value(), List.of(Drink.Type.COFFEE)));
        this.register(BuiltinConditions.IS_TEA, new Condition<>(EspressoConditionTemplates.DRINK_TYPE.value(), List.of(Drink.Type.TEA)));
        this.register(BuiltinConditions.IS_LATTE, new Condition<>(
            EspressoConditionTemplates.ALL.value(),
            List.of(ConditionHolder.indirect(BuiltinConditions.IS_COFFEE), ConditionHolder.indirect(BuiltinConditions.HAS_MILK))
        ));
        this.register(BuiltinConditions.IS_DRINK_OPAQUE, new Condition<>(
            EspressoConditionTemplates.ANY.value(),
            List.of(ConditionHolder.indirect(BuiltinConditions.IS_COFFEE), ConditionHolder.indirect(BuiltinConditions.HAS_MILK))
        ));
    }

    private void register(ResourceLocation location, Condition<?> condition) {
        this.entries.add(new Entry(location, condition));
    }

    @Override
    public String getName() {
        return "Espresso condition provider";
    }

    private record Entry(ResourceLocation location, Condition<?> condition) {
    }
}
