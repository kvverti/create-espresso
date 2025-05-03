package systems.thedawn.espresso;

import com.simibubi.create.content.fluids.VirtualFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class EspressoFluids {
    private static final int COFFEE_TEMPERATURE_KELVIN = 363;
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Espresso.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Espresso.MODID);
    public static final DeferredHolder<FluidType, ?> HOT_WATER = FLUID_TYPES.register(
        "hot_water",
        () -> new FluidType(FluidType.Properties.create()
            .temperature(COFFEE_TEMPERATURE_KELVIN)
            .canSwim(true)
            .canDrown(true)
            .supportsBoating(true)
        ));
    public static final DeferredHolder<FluidType, ?> ESPRESSO = FLUID_TYPES.register(
        "espresso",
        () -> new FluidType(FluidType.Properties.create()
            .temperature(COFFEE_TEMPERATURE_KELVIN)
            .canSwim(false)
            .canDrown(true)
            .supportsBoating(false)
        ));

    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_HOT_WATER =
        FLUIDS.register("hot_water", () -> new BaseFlowingFluid.Source(hotWaterProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_HOT_WATER =
        FLUIDS.register("flowing_hot_water", () -> new BaseFlowingFluid.Flowing(hotWaterProperties()));

    private static BaseFlowingFluid.Properties hotWaterProperties() {
        return new BaseFlowingFluid.Properties(HOT_WATER, SOURCE_HOT_WATER, FLOWING_HOT_WATER)
            .block(EspressoBlocks.HOT_WATER)
            .bucket(EspressoItems.HOT_WATER_BUCKET);
    }

    public static final DeferredHolder<Fluid, VirtualFluid> SOURCE_MIXED_COFFEE =
        FLUIDS.register("mixed_coffee", () -> VirtualFluid.createSource(mixedCoffeeProperties()));
    public static final DeferredHolder<Fluid, VirtualFluid> FLOWING_MIXED_COFFEE =
        FLUIDS.register("flowing_mixed_coffee", () -> VirtualFluid.createFlowing(mixedCoffeeProperties()));

    private static BaseFlowingFluid.Properties mixedCoffeeProperties() {
        return new BaseFlowingFluid.Properties(ESPRESSO, SOURCE_MIXED_COFFEE, FLOWING_MIXED_COFFEE);
    }

    public static final DeferredHolder<Fluid, VirtualFluid> SOURCE_POUR_OVER =
        FLUIDS.register("pour_over", () -> VirtualFluid.createSource(pourOverProperties()));
    public static final DeferredHolder<Fluid, VirtualFluid> FLOWING_POUR_OVER =
        FLUIDS.register("flowing_pour_over", () -> VirtualFluid.createFlowing(pourOverProperties()));

    private static BaseFlowingFluid.Properties pourOverProperties() {
        return new BaseFlowingFluid.Properties(ESPRESSO, SOURCE_POUR_OVER, FLOWING_POUR_OVER);
    }
}
