package systems.thedawn.espresso.recipe;

import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Inputs required for a steeping recipe
 * @param fluid the fluid being diffused into
 * @param steeped the diffusing item
 */
public record SteepingRecipeInput(FluidStack fluid, ItemStack steeped) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if(index == 0) {
            return this.steeped;
        }
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int size() {
        return 1;
    }
}
