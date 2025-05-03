package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoFluids;
import systems.thedawn.espresso.EspressoItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

public class EspressoRecipeProvider extends RecipeProvider {
    public EspressoRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // water -> hot water
        new ProcessingRecipeBuilder<>(MixingRecipe::new, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "hot_water"))
            .withFluidIngredients(FluidIngredient.fromTag(FluidTags.WATER, 10))
            .requiresHeat(HeatCondition.HEATED)
            .averageProcessingDuration()
            .withFluidOutputs(new FluidStack(EspressoFluids.SOURCE_HOT_WATER, 10))
            .build(recipeOutput);

        this.buildCoffeeToolRecipes(recipeOutput);
        this.buildCoffeePlantRecipes(recipeOutput);
        this.buildMixedCoffeeRecipe(recipeOutput);
        this.buildPourOverRecipe(recipeOutput);

        // coffee_beans -> coffee_grounds
        new ProcessingRecipeBuilder<>(MillingRecipe::new, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "coffee_grounds"))
            .withItemIngredients(Ingredient.of(EspressoItems.COFFEE_BEANS))
            .withSingleItemOutput(new ItemStack(EspressoItems.COFFEE_GROUNDS.get()))
            .averageProcessingDuration()
            .build(recipeOutput);
        new ProcessingRecipeBuilder<>(CrushingRecipe::new, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "coffee_grounds"))
            .withItemIngredients(Ingredient.of(EspressoItems.COFFEE_BEANS))
            .withSingleItemOutput(new ItemStack(EspressoItems.COFFEE_GROUNDS.get()))
            .averageProcessingDuration()
            .build(recipeOutput);

        // coffee_grounds -> coffee_brick
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(EspressoItems.COFFEE_GROUNDS),
                RecipeCategory.DECORATIONS,
                EspressoItems.COFFEE_BRICK.value(),
                0f,
                200
            ).unlockedBy("x", has(EspressoItems.COFFEE_GROUNDS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "smelting/espresso_brick"));
        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(EspressoItems.COFFEE_GROUNDS),
                RecipeCategory.DECORATIONS,
                EspressoItems.COFFEE_BRICK.value(),
                0f,
                100
            ).unlockedBy("x", has(EspressoItems.COFFEE_GROUNDS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "blasting/espresso_brick"));

        // coffee_brick -> coffee_bricks
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(EspressoItems.COFFEE_BRICKS.value(), 4))
            .pattern("##")
            .pattern("##")
            .define('#', EspressoItems.COFFEE_BRICK)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICK))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "crafting/coffee_bricks"));

        // coffee_bricks -> coffee_brick_slab
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(EspressoItems.COFFEE_BRICK_SLAB.value(), 6))
            .pattern("###")
            .define('#', EspressoItems.COFFEE_BRICKS)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "crafting/coffee_brick_slab"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(EspressoItems.COFFEE_BRICKS), RecipeCategory.BUILDING_BLOCKS, EspressoItems.COFFEE_BRICK_SLAB, 2)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "cutting/coffee_brick_slab"));

        // coffee_bricks -> coffee_brick_stairs
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(EspressoItems.COFFEE_BRICK_STAIRS.value(), 4))
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .define('#', EspressoItems.COFFEE_BRICKS)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "crafting/coffee_brick_stairs"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(EspressoItems.COFFEE_BRICKS), RecipeCategory.BUILDING_BLOCKS, EspressoItems.COFFEE_BRICK_STAIRS)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "cutting/coffee_brick_stairs"));
    }

    /**
     * Builds recipes defining direct processing of the coffee plant and its extracts.
     */
    private void buildCoffeePlantRecipes(RecipeOutput recipeOutput) {
        // coffee_cherry -> coffee_pit + coffee_paste
        new ProcessingRecipeBuilder<>(MixingRecipe::new, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "mix_coffee_cherry"))
            .withItemIngredients(Ingredient.of(EspressoItems.COFFEE_CHERRY))
            .withItemOutputs(
                new ProcessingOutput(EspressoItems.COFFEE_PASTE.value(), 1, 0.5f),
                new ProcessingOutput(EspressoItems.COFFEE_PIT.value(), 1, 1f)
            )
            .build(recipeOutput);

        // coffee_pit -> coffee_beans
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(EspressoItems.COFFEE_PIT),
                RecipeCategory.MISC,
                EspressoItems.COFFEE_BEANS.get(),
                0.2f,
                200
            ).unlockedBy("x", has(EspressoItems.COFFEE_PIT))
            .save(recipeOutput, Espresso.MODID + ":smelting/coffee_beans");
        SimpleCookingRecipeBuilder.smoking(
                Ingredient.of(EspressoItems.COFFEE_PIT),
                RecipeCategory.MISC,
                EspressoItems.COFFEE_BEANS.get(),
                0.2f,
                100
            ).unlockedBy("x", has(EspressoItems.COFFEE_PIT))
            .save(recipeOutput, Espresso.MODID + ":smoking/coffee_beans");
        SimpleCookingRecipeBuilder.campfireCooking(
                Ingredient.of(EspressoItems.COFFEE_PIT),
                RecipeCategory.MISC,
                EspressoItems.COFFEE_BEANS.get(),
                0.2f,
                200
            ).unlockedBy("x", has(EspressoItems.COFFEE_PIT))
            .save(recipeOutput, Espresso.MODID + ":campfire/coffee_beans");
    }

    private void buildCoffeeToolRecipes(RecipeOutput recipeOutput) {
        // paper -> coffee filter
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EspressoItems.COFFEE_FILTER, 3)
            .pattern("# #")
            .pattern(" # ")
            .define('#', Items.PAPER)
            .unlockedBy("x", has(Items.PAPER))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "crafting/coffee_filter"));
    }

    private void buildMixedCoffeeRecipe(RecipeOutput recipeOutput) {
        // mix mixed coffee
        new ProcessingRecipeBuilder<>(MixingRecipe::new, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "mixed_coffee"))
            .withItemIngredients(
                Ingredient.of(EspressoItems.COFFEE_GROUNDS),
                Ingredient.of(EspressoItems.COFFEE_GROUNDS),
                Ingredient.of(EspressoItems.COFFEE_GROUNDS),
                Ingredient.of(EspressoItems.COFFEE_GROUNDS))
            .withFluidIngredients(FluidIngredient.fromFluid(EspressoFluids.SOURCE_HOT_WATER.value(), 250))
            .averageProcessingDuration()
            .withItemOutputs(new ProcessingOutput(EspressoItems.SPENT_COFFEE_GROUNDS.value(), 4, 1f))
            .withFluidOutputs(new FluidStack(EspressoFluids.SOURCE_MIXED_COFFEE, 250))
            .build(recipeOutput);

        this.buildFillEmptyRecipes(recipeOutput, EspressoFluids.SOURCE_MIXED_COFFEE, EspressoItems.MIXED_COFFEE_BOTTLE);
    }

    private void buildPourOverRecipe(RecipeOutput recipeOutput) {
        // pour over setup
        new SequencedAssemblyRecipeBuilder(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "pour_over_setup"))
            .require(Items.GLASS_BOTTLE)
            .transitionTo(EspressoItems.INCOMPLETE_POUR_OVER_COFFEE_SETUP)
            .addStep(DeployerApplicationRecipe::new, builder ->
                builder.require(EspressoItems.COFFEE_FILTER))
            .addStep(DeployerApplicationRecipe::new, builder ->
                builder.require(EspressoItems.COFFEE_GROUNDS))
            .addStep(DeployerApplicationRecipe::new, builder ->
                builder.require(EspressoItems.COFFEE_GROUNDS))
            .addStep(FillingRecipe::new, builder ->
                builder.require(EspressoFluids.SOURCE_HOT_WATER.value(), 250))
            .loops(1)
            .addOutput(EspressoItems.POUR_OVER_COFFEE_SETUP, 1f)
            .build(recipeOutput);

        // pour over bottle from setup
        new ProcessingRecipeBuilder<>(CuttingRecipe::new, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "pour_over_bottle"))
            .withItemIngredients(Ingredient.of(EspressoItems.POUR_OVER_COFFEE_SETUP))
            .averageProcessingDuration()
            .withItemOutputs(
                new ProcessingOutput(EspressoItems.POUR_OVER_COFFEE_BOTTLE.value(), 1, 1f),
                new ProcessingOutput(EspressoItems.SPENT_COFFEE_GROUNDS.value(), 2, 1f),
                new ProcessingOutput(EspressoItems.USED_COFFEE_FILTER.value(), 1, 1f))
            .build(recipeOutput);

        this.buildFillEmptyRecipes(recipeOutput, EspressoFluids.SOURCE_POUR_OVER, EspressoItems.POUR_OVER_COFFEE_BOTTLE);
    }

    private void buildFillEmptyRecipes(RecipeOutput recipeOutput, DeferredHolder<Fluid, ?> fluid, DeferredItem<?> bottle) {
        // drain bottle
        new ProcessingRecipeBuilder<>(EmptyingRecipe::new, fluid.getId())
            .withItemIngredients(Ingredient.of(bottle))
            .withFluidOutputs(new FluidStack(fluid, 250))
            .averageProcessingDuration()
            .withItemOutputs(new ProcessingOutput(Items.GLASS_BOTTLE, 1, 1f))
            .build(recipeOutput);

        // fill bottle
        new ProcessingRecipeBuilder<>(FillingRecipe::new, bottle.getId())
            .withFluidIngredients(FluidIngredient.fromFluid(fluid.value(), 250))
            .withItemIngredients(Ingredient.of(Items.GLASS_BOTTLE))
            .averageProcessingDuration()
            .withItemOutputs(new ProcessingOutput(bottle.value(), 1, 1f))
            .build(recipeOutput);
    }
}
