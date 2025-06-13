package systems.thedawn.espresso.datagen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.simibubi.create.AllItems;
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
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.EspressoFluids;
import systems.thedawn.espresso.EspressoItems;
import systems.thedawn.espresso.drink.*;
import systems.thedawn.espresso.recipe.DrinkLevelingRecipe;
import systems.thedawn.espresso.recipe.DrinkModificationRecipe;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
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
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        // water -> hot water
        new StandardProcessingRecipe.Builder<>(MixingRecipe::new, Espresso.modLoc("hot_water"))
            .withFluidIngredients(FluidIngredient.fromTag(FluidTags.WATER, 10))
            .requiresHeat(HeatCondition.HEATED)
            .averageProcessingDuration()
            .withFluidOutputs(new FluidStack(EspressoFluids.SOURCE_HOT_WATER, 10))
            .build(recipeOutput);

        // milk -> hot milk
        new StandardProcessingRecipe.Builder<>(MixingRecipe::new, Espresso.modLoc("hot_milk"))
            .withFluidIngredients(FluidIngredient.fromFluid(NeoForgeMod.MILK.value(), 10))
            .requiresHeat(HeatCondition.HEATED)
            .averageProcessingDuration()
            .withFluidOutputs(new FluidStack(EspressoFluids.SOURCE_HOT_MILK, 10))
            .build(recipeOutput);
        this.buildStandardBottleRecipes(recipeOutput, EspressoFluids.SOURCE_HOT_MILK, EspressoItems.HOT_MILK_BOTTLE);

        this.buildCoffeeToolRecipes(recipeOutput);
        this.buildCoffeePlantRecipes(recipeOutput);
        this.buildIceRecipes(recipeOutput);
        this.buildColdBrewCoffeeRecipe(recipeOutput, registries);
        this.buildPourOverRecipe(recipeOutput, registries);
        this.buildMugLevelingRecipes(recipeOutput, registries, BuiltinEspressoDrinks.COLD_BREW, 250);
        this.buildMugLevelingRecipes(recipeOutput, registries, BuiltinEspressoDrinks.POUR_OVER, 250);
        this.buildMugModificationRecipe(recipeOutput, registries, EspressoItems.ICE_CUBES, BuiltinDrinkModifiers.ICE);
        this.buildMugModificationRecipe(recipeOutput, registries, EspressoFluids.SOURCE_HOT_MILK, 125, BuiltinDrinkModifiers.MILK);
        this.buildTallGlassLevelingRecipes(recipeOutput, registries, BuiltinEspressoDrinks.COLD_BREW, 250);
        this.buildTallGlassLevelingRecipes(recipeOutput, registries, BuiltinEspressoDrinks.POUR_OVER, 250);
        this.buildTallGlassModificationRecipe(recipeOutput, registries, EspressoFluids.SOURCE_HOT_MILK, 125, BuiltinDrinkModifiers.MILK);

        // coffee_beans -> coffee_grounds
        new StandardProcessingRecipe.Builder<>(MillingRecipe::new, Espresso.modLoc("coffee_grounds"))
            .withItemIngredients(Ingredient.of(EspressoItems.COFFEE_BEANS))
            .withSingleItemOutput(new ItemStack(EspressoItems.COFFEE_GROUNDS.get()))
            .averageProcessingDuration()
            .build(recipeOutput);
        new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, Espresso.modLoc("coffee_grounds"))
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
            .save(recipeOutput, Espresso.modLoc("smelting/espresso_brick"));
        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(EspressoItems.COFFEE_GROUNDS),
                RecipeCategory.DECORATIONS,
                EspressoItems.COFFEE_BRICK.value(),
                0f,
                100
            ).unlockedBy("x", has(EspressoItems.COFFEE_GROUNDS))
            .save(recipeOutput, Espresso.modLoc("blasting/espresso_brick"));

        // coffee_brick -> coffee_bricks
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(EspressoItems.COFFEE_BRICKS.value(), 4))
            .pattern("##")
            .pattern("##")
            .define('#', EspressoItems.COFFEE_BRICK)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICK))
            .save(recipeOutput, Espresso.modLoc("crafting/coffee_bricks"));

        // coffee_bricks -> coffee_brick_slab
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(EspressoItems.COFFEE_BRICK_SLAB.value(), 6))
            .pattern("###")
            .define('#', EspressoItems.COFFEE_BRICKS)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, Espresso.modLoc("crafting/coffee_brick_slab"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(EspressoItems.COFFEE_BRICKS), RecipeCategory.BUILDING_BLOCKS, EspressoItems.COFFEE_BRICK_SLAB, 2)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, Espresso.modLoc("cutting/coffee_brick_slab"));

        // coffee_bricks -> coffee_brick_stairs
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(EspressoItems.COFFEE_BRICK_STAIRS.value(), 4))
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .define('#', EspressoItems.COFFEE_BRICKS)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, Espresso.modLoc("crafting/coffee_brick_stairs"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(EspressoItems.COFFEE_BRICKS), RecipeCategory.BUILDING_BLOCKS, EspressoItems.COFFEE_BRICK_STAIRS)
            .unlockedBy("x", has(EspressoItems.COFFEE_BRICKS))
            .save(recipeOutput, Espresso.modLoc("cutting/coffee_brick_stairs"));
    }

    /**
     * Builds recipes defining direct processing of the coffee plant and its extracts.
     */
    private void buildCoffeePlantRecipes(RecipeOutput recipeOutput) {
        // coffee_cherry -> coffee_pit + coffee_paste
        new StandardProcessingRecipe.Builder<>(MixingRecipe::new, Espresso.modLoc("mix_coffee_cherry"))
            .withItemIngredients(Ingredient.of(EspressoItems.COFFEE_CHERRY))
            .withItemOutputs(
                new ProcessingOutput(EspressoItems.COFFEE_PASTE.value(), 1, 1f),
                new ProcessingOutput(EspressoItems.COFFEE_PASTE.value(), 1, 0.25f),
                new ProcessingOutput(EspressoItems.COFFEE_PIT.value(), 1, 0.5f)
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
            .save(recipeOutput, Espresso.modLoc("crafting/coffee_filter"));

        // steeper
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EspressoItems.STEEPER, 1)
            .pattern("---")
            .pattern("O#O")
            .pattern("OOO")
            .define('-', AllItems.IRON_SHEET)
            .define('O', Items.GLASS)
            .define('#', Items.IRON_BARS)
            .unlockedBy("x", has(Items.GLASS))
            .save(recipeOutput, Espresso.modLoc("crafting/steeper"));
    }

    private void buildIceRecipes(RecipeOutput recipeOutput) {
        // #ice -> ice cubes
        new StandardProcessingRecipe.Builder<>(CuttingRecipe::new, Espresso.modLoc("ice_cubes"))
            .withItemIngredients(Ingredient.of(Items.ICE))
            .averageProcessingDuration()
            .withItemOutputs(new ProcessingOutput(EspressoItems.ICE_CUBES.value(), 6, 1f))
            .build(recipeOutput);

        // ice cubes -> crushed ice
        new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, Espresso.modLoc("crushed_ice"))
            .withItemIngredients(Ingredient.of(EspressoItems.ICE_CUBES))
            .averageProcessingDuration()
            .withItemOutputs(
                new ProcessingOutput(EspressoItems.CRUSHED_ICE.value(), 1, 1f),
                new ProcessingOutput(EspressoItems.CRUSHED_ICE.value(), 1, 0.25f))
            .build(recipeOutput);
    }

    private void buildColdBrewCoffeeRecipe(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        var dirtyColdBrew = registries.holderOrThrow(BuiltinEspressoDrinks.DIRTY_COLD_BREW);
        this.buildStandardDrinkBottleRecipes(recipeOutput, "dirty_cold_brew_bottle", dirtyColdBrew);

        // mix dirty cold brew
        var dirtyColdBrewFluid = new FluidStack(EspressoFluids.SOURCE_DRINK, 10);
        dirtyColdBrewFluid.set(EspressoDataComponentTypes.DRINK_BASE, dirtyColdBrew);
        new StandardProcessingRecipe.Builder<>(MixingRecipe::new, Espresso.modLoc("dirty_cold_brew"))
            .withItemIngredients(Ingredient.of(EspressoItems.COFFEE_GROUNDS))
            .withFluidIngredients(FluidIngredient.fromTag(FluidTags.WATER, 10))
            .averageProcessingDuration()
            .withItemOutputs(new ProcessingOutput(EspressoItems.COFFEE_GROUNDS.value(), 1, 0.75f))
            .withFluidOutputs(dirtyColdBrewFluid)
            .build(recipeOutput);

        // compact (clean) cold brew
        var coldBrew = registries.holderOrThrow(BuiltinEspressoDrinks.COLD_BREW);
        this.buildStandardDrinkBottleRecipes(recipeOutput, "cold_brew_bottle", coldBrew);
        var dirtyColdBrewInput = new FluidStack(EspressoFluids.SOURCE_DRINK, 250);
        dirtyColdBrewInput.set(EspressoDataComponentTypes.DRINK_BASE, dirtyColdBrew);
        var coldBrewFluid = new FluidStack(EspressoFluids.SOURCE_DRINK, 250);
        coldBrewFluid.set(EspressoDataComponentTypes.DRINK_BASE, coldBrew);
        new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, Espresso.modLoc("cold_brew"))
            .withFluidIngredients(FluidIngredient.fromFluidStack(dirtyColdBrewInput))
            .averageProcessingDuration()
            .withItemOutputs(new ProcessingOutput(EspressoItems.SPENT_COFFEE_GROUNDS.value(), 6, 1f))
            .withFluidOutputs(coldBrewFluid)
            .build(recipeOutput);
    }

    private void buildPourOverRecipe(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        var pourOver = registries.holderOrThrow(BuiltinEspressoDrinks.POUR_OVER);
        this.buildStandardDrinkBottleRecipes(recipeOutput, "pour_over_bottle", pourOver);

        // pour over setup
        new SequencedAssemblyRecipeBuilder(Espresso.modLoc("pour_over_setup"))
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
        var pourOverBottle = new ItemStack(EspressoItems.DRINK_BOTTLE.value());
        pourOverBottle.set(EspressoDataComponentTypes.DRINK_BASE, pourOver);
        new StandardProcessingRecipe.Builder<>(CuttingRecipe::new, Espresso.modLoc("pour_over_bottle"))
            .withItemIngredients(Ingredient.of(EspressoItems.POUR_OVER_COFFEE_SETUP))
            .averageProcessingDuration()
            .withItemOutputs(
                new ProcessingOutput(pourOverBottle, 1f),
                new ProcessingOutput(EspressoItems.SPENT_COFFEE_GROUNDS.value(), 2, 1f),
                new ProcessingOutput(EspressoItems.USED_COFFEE_FILTER.value(), 1, 1f))
            .build(recipeOutput);
    }

    private void buildStandardDrinkBottleRecipes(RecipeOutput recipeOutput, String name, Holder<Drink> component) {
        // drain bottle
        var fluidStack = new FluidStack(EspressoFluids.SOURCE_DRINK, 250);
        fluidStack.set(EspressoDataComponentTypes.DRINK_BASE, component);
        var bottle = new ItemStack(EspressoItems.DRINK_BOTTLE.value());
        bottle.set(EspressoDataComponentTypes.DRINK_BASE, component);

        new StandardProcessingRecipe.Builder<>(EmptyingRecipe::new, Espresso.modLoc(name))
            .withItemIngredients(DataComponentIngredient.of(false, EspressoDataComponentTypes.DRINK_BASE, component, EspressoItems.DRINK_BOTTLE))
            .withFluidOutputs(fluidStack)
            .withItemOutputs(new ProcessingOutput(Items.GLASS_BOTTLE, 1, 1f))
            .build(recipeOutput);

        // fill bottle
        new StandardProcessingRecipe.Builder<>(FillingRecipe::new, Espresso.modLoc(name))
            .withFluidIngredients(FluidIngredient.fromFluidStack(fluidStack))
            .withItemIngredients(Ingredient.of(Items.GLASS_BOTTLE))
            .withItemOutputs(new ProcessingOutput(bottle, 1f))
            .build(recipeOutput);
    }

    private void buildStandardBottleRecipes(RecipeOutput recipeOutput, Holder<? extends Fluid> fluid, Holder<? extends Item> filledBottle) {
        var name = Objects.requireNonNull(filledBottle.getKey()).location().getPath();
        new StandardProcessingRecipe.Builder<>(EmptyingRecipe::new, Espresso.modLoc(name))
            .withItemIngredients(Ingredient.of(filledBottle.value()))
            .withItemOutputs(new ProcessingOutput(Items.GLASS_BOTTLE, 1, 1f))
            .withFluidOutputs(new FluidStack(fluid.value(), 250))
            .build(recipeOutput);
        new StandardProcessingRecipe.Builder<>(FillingRecipe::new, Espresso.modLoc(name))
            .withItemIngredients(Ingredient.of(Items.GLASS_BOTTLE))
            .withFluidIngredients(FluidIngredient.fromFluid(fluid.value(), 250))
            .withItemOutputs(new ProcessingOutput(filledBottle.value(), 1, 1f))
            .build(recipeOutput);
    }

    private void buildMugLevelingRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries, ResourceKey<Drink> drinkKey, int amount) {
        this.buildDrinkLevelingRecipes(recipeOutput, registries, EspressoItems.COFFEE_MUG, EspressoItems.FILLED_COFFEE_MUG, drinkKey, amount);
    }

    private void buildTallGlassLevelingRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries, ResourceKey<Drink> drinkKey, int amount) {
        this.buildDrinkLevelingRecipes(recipeOutput, registries, EspressoItems.TALL_GLASS, EspressoItems.FILLED_TALL_GLASS, drinkKey, amount);
    }

    private void buildDrinkLevelingRecipes(RecipeOutput recipeOutput,
                                           HolderLookup.Provider registries,
                                           Holder<? extends Item> emptyContainer,
                                           Holder<? extends Item> filledContainer,
                                           ResourceKey<Drink> drinkKey,
                                           int amount) {
        var name = drinkKey.location().getPath() + "_" + Objects.requireNonNull(filledContainer.getKey()).location().getPath();
        // initial filling
        var drinkBase = registries.holderOrThrow(drinkKey);
        var drinkFluid = new FluidStack(EspressoFluids.SOURCE_DRINK, amount);
        var filledContainerStack = new ItemStack(filledContainer.value());
        drinkFluid.set(EspressoDataComponentTypes.DRINK_BASE, drinkBase);
        filledContainerStack.set(EspressoDataComponentTypes.DRINK, DrinkComponent.initial(drinkBase));
        new StandardProcessingRecipe.Builder<>(FillingRecipe::new, Espresso.modLoc(name))
            .withItemIngredients(Ingredient.of(emptyContainer.value()))
            .withFluidIngredients(FluidIngredient.fromFluidStack(drinkFluid))
            .withItemOutputs(new ProcessingOutput(filledContainerStack, 1f))
            .build(recipeOutput);

        // leveling
        var levelingRecipe = new DrinkLevelingRecipe(Ingredient.of(filledContainer.value()), drinkBase, amount);
        recipeOutput.accept(Espresso.modLoc("drink_leveling/" + name), levelingRecipe, null);
    }

    private void buildMugModificationRecipe(RecipeOutput recipeOutput, HolderLookup.Provider registries, Holder<? extends Item> applied, ResourceKey<DrinkModifier> modifier) {
        this.buildSpecificModificationRecipe(recipeOutput, registries, EspressoItems.FILLED_COFFEE_MUG, applied, modifier);
    }

    private void buildMugModificationRecipe(RecipeOutput recipeOutput, HolderLookup.Provider registries, Holder<? extends Fluid> applied, int amount, ResourceKey<DrinkModifier> modifier) {
        this.buildSpecificModificationRecipe(recipeOutput, registries, EspressoItems.FILLED_COFFEE_MUG, applied, amount, modifier);
    }

    private void buildTallGlassModificationRecipe(RecipeOutput recipeOutput, HolderLookup.Provider registries, Holder<? extends Fluid> applied, int amount, ResourceKey<DrinkModifier> modifier) {
        this.buildSpecificModificationRecipe(recipeOutput, registries, EspressoItems.FILLED_TALL_GLASS, applied, amount, modifier);
    }

    private void buildSpecificModificationRecipe(RecipeOutput recipeOutput,
                                                 HolderLookup.Provider registries,
                                                 Holder<? extends Item> container,
                                                 Holder<? extends Item> applied,
                                                 ResourceKey<DrinkModifier> modifier) {
        var name = modifier.location().getPath() + "_" + Objects.requireNonNull(container.getKey()).location().getPath();
        var modificationRecipe = new DrinkModificationRecipe(
            Ingredient.of(container.value()),
            registries.holderOrThrow(modifier),
            Ingredient.of(applied.value()),
            null);
        recipeOutput.accept(Espresso.modLoc("drink_modification/" + name), modificationRecipe, null);
    }

    private void buildSpecificModificationRecipe(RecipeOutput recipeOutput,
                                                 HolderLookup.Provider registries,
                                                 Holder<? extends Item> container,
                                                 Holder<? extends Fluid> applied,
                                                 int amount,
                                                 ResourceKey<DrinkModifier> modifier) {
        var name = modifier.location().getPath() + "_" + Objects.requireNonNull(container.getKey()).location().getPath();
        var modificationRecipe = new DrinkModificationRecipe(
            Ingredient.of(container.value()),
            registries.holderOrThrow(modifier),
            null,
            FluidIngredient.fromFluid(applied.value(), amount));
        recipeOutput.accept(Espresso.modLoc("drink_modification/" + name), modificationRecipe, null);
    }
}
