package systems.thedawn.espresso.item;

import java.util.List;

import systems.thedawn.espresso.BuiltinEspressoDrinks;
import systems.thedawn.espresso.Drink;
import systems.thedawn.espresso.DrinkComponent;
import systems.thedawn.espresso.EspressoDataComponentTypes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class DrinkItem extends Item {
    public DrinkItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        var component = stack.getOrDefault(EspressoDataComponentTypes.DRINK, DrinkComponent.EMPTY);
        var drinkType = component.drink().value().type();
        return getDrinkDescriptionId(this, drinkType);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var component = stack.getOrDefault(EspressoDataComponentTypes.DRINK, DrinkComponent.EMPTY);
        var key = component.drink().getKey();
        if(key == null) {
            key = BuiltinEspressoDrinks.EMPTY;
        }
        var text = Drink.getDescription(key);
        tooltipComponents.add(text);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static String getDrinkDescriptionId(Item item, Drink.Type type) {
        return String.format("%s.%s", item.getDescriptionId(), type.getSerializedName());
    }
}
