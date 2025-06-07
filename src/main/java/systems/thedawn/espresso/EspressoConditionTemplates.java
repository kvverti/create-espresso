package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.client.condition.*;

public class EspressoConditionTemplates {
    public static final DeferredRegister<ConditionTemplate<?>> CONDITION_TEMPLATES = DeferredRegister.create(EspressoRegistries.DRINK_CONDITION_TEMPLATES, Espresso.MODID);

    public static final DeferredHolder<ConditionTemplate<?>, TrivialTemplate> TRIVIAL = CONDITION_TEMPLATES.register("trivial", TrivialTemplate::new);
    public static final DeferredHolder<ConditionTemplate<?>, DrinkTypeTemplate> DRINK_TYPE = CONDITION_TEMPLATES.register("has_drink_type", DrinkTypeTemplate::new);
    public static final DeferredHolder<ConditionTemplate<?>, DrinkTemplate> DRINK = CONDITION_TEMPLATES.register("has_drink", DrinkTemplate::new);
    public static final DeferredHolder<ConditionTemplate<?>, ModifierTemplate> MODIFIER = CONDITION_TEMPLATES.register("has_modifier", ModifierTemplate::new);
    public static final DeferredHolder<ConditionTemplate<?>, AllTemplate> ALL = CONDITION_TEMPLATES.register("all", AllTemplate::new);
    public static final DeferredHolder<ConditionTemplate<?>, AnyTemplate> ANY = CONDITION_TEMPLATES.register("any", AnyTemplate::new);
}
