package systems.thedawn.espresso.drink.condition;

import java.util.List;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoConditionTemplates;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.BuiltinDrinkModifiers;
import systems.thedawn.espresso.drink.Drink;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

public class BuiltinConditions {
    public static final ResourceKey<Condition<?>> HAS_DRINK = register("has_drink");
    public static final ResourceKey<Condition<?>> HAS_MILK = register("has_milk");
    public static final ResourceKey<Condition<?>> HAS_BUBBLES = register("has_bubbles");
    public static final ResourceKey<Condition<?>> HAS_ICE = register("has_ice");
    public static final ResourceKey<Condition<?>> IS_COFFEE = register("is_coffee");

    private static ResourceKey<Condition<?>> register(String name) {
        return ResourceKey.create(EspressoRegistries.DRINK_CONDITIONS, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, name));
    }

    public static void bootstrapConditions(BootstrapContext<Condition<?>> ctx) {
        ctx.register(HAS_DRINK, new Condition<>(EspressoConditionTemplates.TRIVIAL.value(), Unit.INSTANCE));
        ctx.register(HAS_MILK, new Condition<>(EspressoConditionTemplates.MODIFIER.value(), List.of(BuiltinDrinkModifiers.MILK)));
        ctx.register(HAS_BUBBLES, new Condition<>(EspressoConditionTemplates.MODIFIER.value(), List.of(BuiltinDrinkModifiers.BUBBLES)));
        ctx.register(HAS_ICE, new Condition<>(EspressoConditionTemplates.MODIFIER.value(), List.of(BuiltinDrinkModifiers.ICE)));
        ctx.register(IS_COFFEE, new Condition<>(EspressoConditionTemplates.DRINK_TYPE.value(), List.of(Drink.Type.COFFEE)));
    }
}
