package systems.thedawn.espresso;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@EventBusSubscriber(modid = Espresso.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class EspressoRegistries {
    public static final ResourceKey<Registry<Drink>> DRINKS =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "drinks"));

    @SubscribeEvent
    public static void dataPackRegistries(DataPackRegistryEvent.NewRegistry ev) {
        ev.dataPackRegistry(DRINKS, Drink.DIRECT_CODEC, Drink.DIRECT_CODEC);
    }
}
