package systems.thedawn.espresso.util;

import java.util.AbstractList;

import net.neoforged.neoforge.items.IItemHandler;

import net.minecraft.world.item.ItemStack;

/**
 * A simple List wrapper over an IItemHandler.
 */
public final class ItemHandlerListView extends AbstractList<ItemStack> {
    private final IItemHandler itemHandler;

    public ItemHandlerListView(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public ItemStack get(int index) {
        return this.itemHandler.getStackInSlot(index);
    }

    @Override
    public int size() {
        return this.itemHandler.getSlots();
    }
}
