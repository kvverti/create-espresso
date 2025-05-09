package systems.thedawn.espresso.recipe;

import javax.annotation.Nullable;

import net.neoforged.neoforge.fluids.FluidStack;
import systems.thedawn.espresso.EspressoRecipeTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class RecipeUtil {
    public static @Nullable FluidInputRecipe<?> findRecipe(Level world, ItemStack drinkHolder, FluidStack fluidStack) {
        var manager = world.getRecipeManager();
        // drink leveling
        var levelInput = new DrinkLevelingRecipeInput(drinkHolder, fluidStack);
        RecipeHolder<? extends FluidInputRecipe<?>> recipe = manager.getRecipeFor(EspressoRecipeTypes.DRINK_LEVEL.value(), levelInput, world).orElse(null);
        if(recipe != null) {
            return recipe.value();
        }

        // drink modification
        var modifyInput = new DrinkModificationRecipeInput(drinkHolder, null, fluidStack);
        recipe = manager.getRecipeFor(EspressoRecipeTypes.DRINK_MODIFY.value(), modifyInput, world).orElse(null);
        if(recipe != null) {
            return recipe.value();
        }

        return null;
    }
}
