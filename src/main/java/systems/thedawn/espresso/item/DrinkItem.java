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
import net.minecraft.world.effect.MobEffectInstance;
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
        if(!level.isClientSide()) {
            var component = stack.get(EspressoDataComponentTypes.DRINK);
            if(component != null) {
                for(var effectInstance : effects(component)) {
                    livingEntity.addEffect(new MobEffectInstance(effectInstance));
                }
            }
        }

        livingEntity.gameEvent(GameEvent.DRINK);

        if(!livingEntity.hasInfiniteMaterials()) {
            return new ItemStack(this.emptyItem);
        }
        return stack;
    }

    private static List<MobEffectInstance> effects(DrinkComponent drink) {
        var effectsFromModifiers = new ArrayList<MobEffectInstance>();
        var baseLengthScale = 1f;
        var baseStrengthOffset = 0;
        switch(drink.level()) {
            case DOUBLE -> baseLengthScale = 2f;
            case TRIPLE -> {
                baseLengthScale = 2f;
                baseStrengthOffset = 1;
            }
        }

        for(var modifier : drink.modifiers()) {
            effectsFromModifiers.addAll(modifier.value().additionalEffects());
            var transform = modifier.value().transform().orElse(null);
            switch(transform) {
                case DrinkModifier.BaseTransform.Lengthen(var scale) -> baseLengthScale *= scale;
                case DrinkModifier.BaseTransform.Strengthen(var level) -> baseStrengthOffset += level;
                case null -> {
                    // nothing
                }
            }
        }
        var effectList = new ArrayList<MobEffectInstance>();
        // update base effects
        for(var effectInstance : drink.base().value().effects()) {
            var duration = effectInstance.getDuration();
            var intensity = effectInstance.getAmplifier();
            effectList.add(new MobEffectInstance(effectInstance.getEffect(), (int) (duration * baseLengthScale), intensity + baseStrengthOffset));
        }
        effectList.addAll(effectsFromModifiers);
        return effectList;
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
