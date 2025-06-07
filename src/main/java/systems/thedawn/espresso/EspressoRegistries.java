package systems.thedawn.espresso;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkModifier;
import systems.thedawn.espresso.client.condition.Condition;
import systems.thedawn.espresso.client.condition.ConditionTemplate;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@EventBusSubscriber(modid = Espresso.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class EspressoRegistries {
    public static final ResourceKey<Registry<Drink>> DRINKS =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "drinks"));
    public static final ResourceKey<Registry<DrinkModifier>> DRINK_MODIFIERS =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "drink_modifiers"));
    public static final ResourceKey<Registry<ConditionTemplate<?>>> DRINK_CONDITION_TEMPLATES =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "drink_condition_templates"));
    public static final ResourceKey<Registry<Condition<?>>> DRINK_CONDITIONS =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "drink_conditions"));

    @SubscribeEvent
    public static void dataPackRegistries(DataPackRegistryEvent.NewRegistry ev) {
        ev.dataPackRegistry(DRINKS, Drink.DIRECT_CODEC, Drink.DIRECT_CODEC);
        ev.dataPackRegistry(DRINK_MODIFIERS, DrinkModifier.DIRECT_CODEC, DrinkModifier.DIRECT_CODEC);
        ev.dataPackRegistry(DRINK_CONDITIONS, Condition.DIRECT_CODEC, Condition.DIRECT_CODEC);
    }

    /**
     * Static (non-dynamic) registry objects.
     */
    public static final class Static {
        public static final Registry<ConditionTemplate<?>> DRINK_CONDITION_TEMPLATES =
            new RegistryBuilder<>(EspressoRegistries.DRINK_CONDITION_TEMPLATES)
                .sync(true)
                .create();
    }

    @SubscribeEvent
    public static void staticRegistries(NewRegistryEvent ev) {
        ev.register(Static.DRINK_CONDITION_TEMPLATES);
    }
}
