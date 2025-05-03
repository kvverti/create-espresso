package systems.thedawn.espresso.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

public class DrinkItem extends PotionItem {
    public DrinkItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return "create_espresso.bottle.placeholder";
    }
}
