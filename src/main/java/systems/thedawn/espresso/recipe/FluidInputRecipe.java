package systems.thedawn.espresso.recipe;

import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface FluidInputRecipe<T extends RecipeInput> extends Recipe<T> {
    int fillAmount();

    ItemStack assembleFromFluid(ItemStack input, FluidStack fluidInput, HolderLookup.Provider registries);
}
