package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.block.steeper.SteeperComponent;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public final class EspressoDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Espresso.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Drink>>> DRINK_BASE =
        DATA_COMPONENT_TYPES.register("drink_base", () ->
            DataComponentType.<Holder<Drink>>builder()
                .persistent(Drink.CODEC)
                .networkSynchronized(Drink.STREAM_CODEC)
                .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DrinkComponent>> DRINK =
        DATA_COMPONENT_TYPES.register("drink", () ->
            DataComponentType.<DrinkComponent>builder()
                .persistent(DrinkComponent.CODEC)
                .networkSynchronized(DrinkComponent.STREAM_CODEC)
                .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SteeperComponent>> STEEPING =
        DATA_COMPONENT_TYPES.register("steeping", () ->
            DataComponentType.<SteeperComponent>builder()
                .persistent(SteeperComponent.CODEC)
                .networkSynchronized(SteeperComponent.STREAM_CODEC)
                .build());
}
