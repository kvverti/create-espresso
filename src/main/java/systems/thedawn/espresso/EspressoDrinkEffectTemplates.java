package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.drink.effect.*;

public final class EspressoDrinkEffectTemplates {
    public static final DeferredRegister<DrinkEffectTemplate<?>> DRINK_EFFECT_TEMPLATES = DeferredRegister.create(EspressoRegistries.DRINK_EFFECT_TEMPLATES, Espresso.MODID);

    public static final DeferredHolder<DrinkEffectTemplate<?>, MobEffectTemplate> MOB_EFFECT = DRINK_EFFECT_TEMPLATES.register("potion_effect", MobEffectTemplate::new);
    public static final DeferredHolder<DrinkEffectTemplate<?>, TriggerTemplate> TRIGGER = DRINK_EFFECT_TEMPLATES.register("trigger", TriggerTemplate::new);
    public static final DeferredHolder<DrinkEffectTemplate<?>, TeleportTemplate> TELEPORT = DRINK_EFFECT_TEMPLATES.register("teleport_in_range", TeleportTemplate::new);
}
