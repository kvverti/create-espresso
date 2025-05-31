package systems.thedawn.espresso.jei;

import java.util.List;
import java.util.function.Supplier;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoItems;
import systems.thedawn.espresso.EspressoRecipeTypes;
import systems.thedawn.espresso.recipe.DrinkModificationRecipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ModificationRecipeCategory implements IRecipeCategory<RecipeHolder<DrinkModificationRecipe>> {
    public static final Supplier<RecipeType<RecipeHolder<DrinkModificationRecipe>>> JEI_TYPE =
        RecipeType.createFromDeferredVanilla(EspressoRecipeTypes.DRINK_MODIFY);

    private final IJeiHelpers helpers;

    public ModificationRecipeCategory(IJeiHelpers helpers) {
        this.helpers = helpers;
    }

    @Override
    public RecipeType<RecipeHolder<DrinkModificationRecipe>> getRecipeType() {
        return JEI_TYPE.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("create_espresso.jei.modification.title");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return helpers.getGuiHelper().createDrawableItemLike(EspressoItems.FILLED_COFFEE_MUG);
    }

    private static final int WIDTH = 100;
    private static final int HEIGHT = 50;
    private static final int SLOT_SIZE = 16;
    private static final String OUTPUT_SLOT = "Output";

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    private static int slotCenter(int pos) {
        return pos - SLOT_SIZE / 2;
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<DrinkModificationRecipe> recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        recipeSlots.get(0).getDisplayedItemStack().ifPresent(input -> {
            var output = recipe.value().modifiedResultStack(input);
            recipeSlots.get(2).createDisplayOverrides()
                .addItemStack(output);
        });
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DrinkModificationRecipe> recipe, IFocusGroup focuses) {
        // drink holder
        builder.addInputSlot()
            .setPosition(slotCenter(WIDTH / 5), slotCenter(HEIGHT / 2) + 10)
            .setStandardSlotBackground()
            .addItemStacks(recipe.value().holderStacks());
        // applied ingredient
        var appliedItem = recipe.value().appliedItem();
        var appliedFluid = recipe.value().appliedFluid();
        var applySlot = builder.addInputSlot()
            .setPosition(slotCenter(WIDTH / 5), slotCenter(HEIGHT / 2) - 10)
            .setStandardSlotBackground();
        if(appliedItem != null) {
            applySlot.addIngredients(appliedItem);
        } else if(appliedFluid != null) {
            applySlot.addIngredients(NeoForgeTypes.FLUID_STACK, appliedFluid.getMatchingFluidStacks());
        }
        // output
        builder.addOutputSlot()
            .setSlotName(OUTPUT_SLOT)
            .setPosition(slotCenter(WIDTH * 4 / 5), slotCenter(HEIGHT / 2))
            .setOutputSlotBackground();
    }

    @Override
    public void draw(RecipeHolder<DrinkModificationRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var arrow = this.helpers.getGuiHelper().getRecipeArrow();
        arrow.draw(guiGraphics, WIDTH / 2 - arrow.getWidth() / 2, HEIGHT / 2 - arrow.getHeight() / 2);
    }
}
