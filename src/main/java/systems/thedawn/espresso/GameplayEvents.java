package systems.thedawn.espresso;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import net.minecraft.world.item.ItemStack;

@EventBusSubscriber(modid = Espresso.MODID)
public final class GameplayEvents {
    @SubscribeEvent
    public static void addWanderingTrades(WandererTradesEvent ev) {
        ev.getGenericTrades().add(new BasicItemListing(3, new ItemStack(EspressoItems.COFFEE_CHERRY.value()), 10, 1));
    }
}
