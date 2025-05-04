package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public final class EspressoDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Espresso.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DrinkComponent>> DRINK =
        DATA_COMPONENT_TYPES.register("drink", () ->
            DataComponentType.<DrinkComponent>builder()
                .persistent(DrinkComponent.CODEC)
                .networkSynchronized(DrinkComponent.STREAM_CODEC)
                .build());
}
