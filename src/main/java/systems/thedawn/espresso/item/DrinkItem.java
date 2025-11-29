package systems.thedawn.espresso.item;

import java.util.ArrayList;
import java.util.List;

import systems.thedawn.espresso.drink.BuiltinEspressoDrinks;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkComponent;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.drink.DrinkModifier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;

public class DrinkItem extends BlockItem {
    private final Item emptyItem;

    public DrinkItem(Block block, Item emptyItem, Properties properties) {
        super(block, properties);
        this.emptyItem = emptyItem;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(!level.isClientSide() && livingEntity instanceof Player player) {
            var component = stack.get(EspressoDataComponentTypes.DRINK);
            var effects = new ArrayList<>(component.base().value().effects());
            var drinkLevel = Math.max(1, component.level().levelIndex());
            var strength = 1d;
            for(var modifierHolder : component.modifiers()) {
                var modifier = modifierHolder.value();
                drinkLevel += modifier.levelOffset();
                strength *= modifier.strengthScale();
                effects.addAll(modifier.additionalEffects());
            }
            for(var effect : effects) {
                effect.apply(player, drinkLevel, strength);
            }
        }

        livingEntity.gameEvent(GameEvent.DRINK);

        if(!livingEntity.hasInfiniteMaterials()) {
            return new ItemStack(this.emptyItem);
        }
        return stack;
    }

    // reset the base translation key to the one in Item.
    @Override
    public String getDescriptionId() {
        return super.getOrCreateDescriptionId();
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        var component = stack.get(EspressoDataComponentTypes.DRINK);
        var drinkType = component == null ? Drink.Type.COFFEE : component.base().value().type();
        return DrinkUtil.getDrinkDescriptionId(this, drinkType);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var component = stack.get(EspressoDataComponentTypes.DRINK);
        if(component != null) {
            var key = component.base().getKey();
            if(key == null) {
                key = BuiltinEspressoDrinks.EMPTY;
            }
            var text = Drink.getDescription(key);
            tooltipComponents.add(text);
            if(component.level() != DrinkComponent.BaseLevel.SINGLE) {
                var level = Component.translatable(component.level().getDescriptionId())
                    .withStyle(ChatFormatting.GRAY);
                tooltipComponents.add(level);
            }
            for(var modifier : component.modifiers()) {
                if(modifier.getKey() != null) {
                    var modifierText = DrinkModifier.getDescription(modifier.getKey());
                    tooltipComponents.add(modifierText);
                }
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
