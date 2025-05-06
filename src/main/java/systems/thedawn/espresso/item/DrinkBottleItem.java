package systems.thedawn.espresso.item;

import java.util.List;

import systems.thedawn.espresso.BuiltinEspressoDrinks;
import systems.thedawn.espresso.Drink;
import systems.thedawn.espresso.EspressoDataComponentTypes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A drink bottle. Distinct from other drink containers in that they
 * can only hold a drink base (not a full drink) and don't have a placed form.
 */
public class DrinkBottleItem extends Item {
    public DrinkBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        var component = stack.get(EspressoDataComponentTypes.DRINK_BASE);
        var drinkType = component == null ? Drink.Type.COFFEE : component.value().type();
        return DrinkUtil.getDrinkDescriptionId(this, drinkType);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var component = stack.get(EspressoDataComponentTypes.DRINK_BASE);
        var key = BuiltinEspressoDrinks.EMPTY;
        if(component != null) {
            var k = component.getKey();
            if(k != null) {
                key = k;
            }
        }
        var text = Drink.getDescription(key);
        tooltipComponents.add(text);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
