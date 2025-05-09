package systems.thedawn.espresso.mixin;

import javax.annotation.Nonnull;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import systems.thedawn.espresso.recipe.RecipeUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FillingBySpout.class)
public class SpoutCustomRecipeMixin {
    @WrapOperation(
        method = "getRequiredAmountForItem",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/fluids/transfer/GenericItemFilling;getRequiredAmountForItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/neoforged/neoforge/fluids/FluidStack;)I"
        )
    )
    private static int espresso$getCustomFillingRecipeAmount(Level world, ItemStack stack, FluidStack availableFluid, Operation<Integer> original) {
        var recipe = RecipeUtil.findRecipe(world, stack, availableFluid);
        if(recipe != null) {
            return recipe.fillAmount();
        }
        return original.call(world, stack, availableFluid);
    }

    @WrapOperation(
        method = "fillItem",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/fluids/transfer/GenericItemFilling;fillItem(Lnet/minecraft/world/level/Level;ILnet/minecraft/world/item/ItemStack;Lnet/neoforged/neoforge/fluids/FluidStack;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack espresso$applyCustomFillingRecipe(Level world, int requiredAmount, ItemStack stack, FluidStack availableFluid, Operation<ItemStack> original) {
        var recipe = RecipeUtil.findRecipe(world, stack, availableFluid);
        if(recipe != null) {
            var output = recipe.assembleFromFluid(stack, availableFluid, world.registryAccess());
            availableFluid.shrink(recipe.fillAmount());
            stack.shrink(1);
            return output;
        }
        return original.call(world, requiredAmount, stack, availableFluid);
    }
}

@Mixin(SpoutBlockEntity.class)
class SpoutCustomRecipeAllowanceMixin extends BlockEntity {
    public SpoutCustomRecipeAllowanceMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Shadow
    private @Nonnull FluidStack getCurrentFluidInTank() {
        throw new AssertionError();
    }

    @ModifyExpressionValue(
        method = { "onItemReceived", "whenItemHeld" },
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/fluids/spout/FillingBySpout;canItemBeFilled(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private boolean espresso$findCustomFillingRecipes(boolean ret, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if(!ret && this.level != null) {
            var recipe = RecipeUtil.findRecipe(this.level, transported.stack, this.getCurrentFluidInTank());
            return recipe != null;
        }
        return ret;
    }
}
