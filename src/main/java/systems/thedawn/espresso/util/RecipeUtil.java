package systems.thedawn.espresso.util;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.block.steeper.SteeperBlock;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class RecipeUtil {
    /**
     * Finds the item result of filling the given container with the given amount of fluid.
     *
     * @param stack       the input stack
     * @param level       the world instance
     * @param filledFluid the fluid to fill the input with
     * @param amount      the amount of available fluid
     * @return the result stack and amount of fluid to be consumed
     */
    @Nullable
    public static FillingResult findFillingResult(ItemStack stack, Level level, FluidStack filledFluid, int amount) {
        var recipe = getFillingRecipe(stack, filledFluid, level);
        if(recipe != null) {
            return new FillingResult(recipe.getResultItem(level.registryAccess()), recipe.getRequiredFluid().getRequiredAmount());
        } else if(GenericItemFilling.canItemBeFilled(level, stack)) {
            var resultStack = GenericItemFilling.fillItem(level, amount, stack.copy(), filledFluid.copy());
            var requiredAmount = GenericItemFilling.getRequiredAmountForItem(level, stack, filledFluid);
            return new FillingResult(resultStack, requiredAmount);
        }
        return null;
    }

    private static @Nullable FillingRecipe getFillingRecipe(ItemStack stack, FluidStack fluid, Level level) {
        var recipes = level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.FILLING.<SingleRecipeInput, FillingRecipe>getType());
        for(var holder : recipes) {
            var recipe = holder.value();
            if(recipe.getRequiredFluid().test(fluid) && recipe.getIngredients().get(0).test(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public record FillingResult(ItemStack resultStack, int requiredFluidAmount) {
    }

    /**
     * Finds the fluid result of emptying the given container.
     *
     * @param stack the input stack
     * @param level the world instance
     * @return the sized result fluid and remaining empty container
     */
    @Nullable
    public static EmptyingResult findEmptyingResult(ItemStack stack, Level level) {
        var recipe = getEmptyingRecipe(stack, level);
        if(recipe != null) {
            return new EmptyingResult(recipe.getResultingFluid(), recipe.getResultItem(level.registryAccess()));
        } else if(GenericItemEmptying.canItemBeEmptied(level, stack)) {
            var result = GenericItemEmptying.emptyItem(level, stack, true);
            var remaining = result.getSecond();
            // work around Create returning the incorrect empty bucket. Ideally we would do more robust checking here
            if(remaining.getItem() instanceof BucketItem) {
                remaining = new ItemStack(Items.BUCKET, remaining.getCount());
            }
            return new EmptyingResult(result.getFirst(), remaining);
        }
        return null;
    }

    private static @Nullable EmptyingRecipe getEmptyingRecipe(ItemStack stack, Level level) {
        var input = new SingleRecipeInput(stack);
        return level.getRecipeManager()
            .getRecipeFor(AllRecipeTypes.EMPTYING.<SingleRecipeInput, EmptyingRecipe>getType(), input, level)
            .map(RecipeHolder::value)
            .filter(recipe -> recipe.getResultingFluid().getAmount() == SteeperBlock.FLUID_CAPACITY)
            .orElse(null);
    }

    public record EmptyingResult(FluidStack resultFluid, ItemStack remainingItem) {
    }
}
