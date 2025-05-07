package systems.thedawn.espresso.recipe;

import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record DrinkLevelingRecipeInput(ItemStack drinkHolder, FluidStack drinkFluid) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if(index == 0) {
            return this.drinkHolder;
        } else {
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public int size() {
        return 1;
    }
}
