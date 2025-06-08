package systems.thedawn.espresso;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import systems.thedawn.espresso.client.condition.ConditionTemplate;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkModifier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

@EventBusSubscriber(modid = Espresso.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class EspressoRegistries {
    public static final ResourceKey<Registry<Drink>> DRINKS =
        ResourceKey.createRegistryKey(Espresso.modLoc("drinks"));
    public static final ResourceKey<Registry<DrinkModifier>> DRINK_MODIFIERS =
        ResourceKey.createRegistryKey(Espresso.modLoc("drink_modifiers"));
    public static final ResourceKey<Registry<ConditionTemplate<?>>> DRINK_CONDITION_TEMPLATES =
        ResourceKey.createRegistryKey(Espresso.modLoc("drink_condition_templates"));

    @SubscribeEvent
    public static void dataPackRegistries(DataPackRegistryEvent.NewRegistry ev) {
        ev.dataPackRegistry(DRINKS, Drink.DIRECT_CODEC, Drink.DIRECT_CODEC);
        ev.dataPackRegistry(DRINK_MODIFIERS, DrinkModifier.DIRECT_CODEC, DrinkModifier.DIRECT_CODEC);
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
