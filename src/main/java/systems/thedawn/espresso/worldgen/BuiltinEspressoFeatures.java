package systems.thedawn.espresso.worldgen;

import java.util.List;

import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.block.CoffeePlantBlock;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public final class BuiltinEspressoFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> COFFEE_PLANT_CONFIG =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, Espresso.modLoc("coffee_plant_patch"));

    public static final ResourceKey<PlacedFeature> COFFEE_PLANT =
        ResourceKey.create(Registries.PLACED_FEATURE, Espresso.modLoc("coffee_plant_patch"));

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        var coffeePlant = new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(
            BlockStateProvider.simple(EspressoBlocks.GROWN_COFFEE_PLANT
                .value()
                .defaultBlockState()
                .setValue(CoffeePlantBlock.AGE, CoffeePlantBlock.MAX_AGE))));

        ctx.register(COFFEE_PLANT_CONFIG, new ConfiguredFeature<>(
            Feature.RANDOM_PATCH,
            new RandomPatchConfiguration(10, 10, 4, Holder.direct(new PlacedFeature(
                Holder.direct(coffeePlant),
                List.of(HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)))))));
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> ctx) {
        var coffeePlantConfig = ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(COFFEE_PLANT_CONFIG);
        ctx.register(COFFEE_PLANT, new PlacedFeature(coffeePlantConfig, List.of(RarityFilter.onAverageOnceEvery(20))));
    }
}
