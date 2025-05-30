package systems.thedawn.espresso.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import net.neoforged.neoforge.items.IItemHandler;

import net.minecraft.world.item.ItemStack;

public final class ItemHandlerUtil {
    /**
     * Collect the non-empty contents of the given inventory into a separate list.
     */
    public static List<ItemStack> nonEmptyContents(IItemHandler inv) {
        var count = inv.getSlots();
        var contents = new ArrayList<ItemStack>();
        for(var i = 0; i < count; i++) {
            var stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                contents.add(stack);
            }
        }
        return contents;
    }

    /**
     * Create a simple List wrapper over an IItemHandler.
     */
    public static List<ItemStack> listView(IItemHandler inv) {
        class ListView extends AbstractList<ItemStack> implements RandomAccess {
            @Override
            public ItemStack get(int index) {
                return inv.getStackInSlot(index);
            }

            @Override
            public int size() {
                return inv.getSlots();
            }
        }
        return new ListView();
    }
}
