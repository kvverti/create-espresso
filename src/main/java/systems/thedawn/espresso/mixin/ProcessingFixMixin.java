package systems.thedawn.espresso.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

/**
 * Patch Create processing recipes to add component patch to the output stack.
 * Fixed in Create 6.0.6
 */
@Mixin(ProcessingOutput.class)
public class ProcessingFixMixin {
    @Shadow
    @Final
    private DataComponentPatch patch;

    @ModifyReturnValue(method = "rollOutput", at = @At("RETURN"))
    private ItemStack espresso$addComponentPatch(ItemStack ret) {
        ret.applyComponents(this.patch);
        return ret;
    }
}
