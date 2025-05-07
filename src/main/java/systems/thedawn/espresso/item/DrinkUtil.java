package systems.thedawn.espresso.item;

import systems.thedawn.espresso.drink.Drink;

import net.minecraft.world.item.Item;

public final class DrinkUtil {
    public static String getDrinkDescriptionId(Item item, Drink.Type type) {
        return String.format("%s.%s", item.getDescriptionId(), type.getSerializedName());
    }
}
