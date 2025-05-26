package systems.thedawn.espresso.recipe;

import java.util.List;

import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record SieveRecipeInput(List<ItemStack> items,
                               FluidStack fluid,
                               boolean hasPress,
                               FilterCondition filter) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }
}
