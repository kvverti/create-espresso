package systems.thedawn.espresso.worldgen;

import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import systems.thedawn.espresso.Espresso;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class BuiltinEspressoBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_COFFEE_PLANT_PATCH =
        ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "add_coffee_plant_patch"));

    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> ctx) {
        var biomes = ctx.lookup(Registries.BIOME);
        var features = ctx.lookup(Registries.PLACED_FEATURE);

        ctx.register(ADD_COFFEE_PLANT_PATCH, new BiomeModifiers.AddFeaturesBiomeModifier(
            biomes.get(BiomeTags.IS_JUNGLE).orElseThrow(),
            HolderSet.direct(features.getOrThrow(BuiltinEspressoFeatures.COFFEE_PLANT)),
            GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }
}
