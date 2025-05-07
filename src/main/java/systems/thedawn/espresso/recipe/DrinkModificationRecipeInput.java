package systems.thedawn.espresso.recipe;

import javax.annotation.Nullable;

import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record DrinkModificationRecipeInput(ItemStack drinkHolder,
                                           @Nullable ItemStack appliedItem,
                                           @Nullable FluidStack appliedFluid) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch(index) {
            case 0 -> this.drinkHolder;
            case 1 -> {
                if(this.appliedItem == null) {
                    throw new IndexOutOfBoundsException(index);
                }
                yield this.appliedItem;
            }
            default -> throw new IndexOutOfBoundsException(index);
        };
    }

    @Override
    public int size() {
        return 1 + (this.appliedItem != null ? 1 : 0);
    }
}
