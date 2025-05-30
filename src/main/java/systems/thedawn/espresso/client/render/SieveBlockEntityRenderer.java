package systems.thedawn.espresso.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import org.joml.Quaternionf;
import systems.thedawn.espresso.block.sieve.SieveBlockEntity;
import systems.thedawn.espresso.util.ItemHandlerUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class SieveBlockEntityRenderer extends SmartBlockEntityRenderer<SieveBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SieveBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void renderSafe(SieveBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderSafe(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        var upperItems = blockEntity.upperInventory();
        var contents = ItemHandlerUtil.nonEmptyContents(upperItems);
        // render items
        if(!contents.isEmpty()) {
            var itemCount = (float) contents.size();
            var downRotation = new Quaternionf().fromAxisAngleRad(1f, 0f, 0f, (float) (Math.PI / 2));
            var circleRotation = new Quaternionf();
            for(int n = 0; n < itemCount; n++) {
                var stack = contents.get(n);
                poseStack.pushPose();
                poseStack.translate(0.5, 0.6, 0.5);
                poseStack.rotateAround(circleRotation.fromAxisAngleRad(0f, 1f, 0f, (n / itemCount) * (float) (2 * Math.PI)), 0f, 0f, 0f);
                poseStack.rotateAround(downRotation, 0f, 0f, 0f);
                this.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
                poseStack.popPose();
            }
        }
        var lowerItem = blockEntity.lowerInventory().getStackInSlot(0);
        if(!lowerItem.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.25, 0.5);
            this.itemRenderer.renderStatic(lowerItem, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }

        // render fluids
        var upperFluid = blockEntity.upperTank().getFluidInTank(0);
        if(!upperFluid.isEmpty()) {
            var amount = upperFluid.getAmount();
            var yMax = 0.75f - 0.00025f * (1000 - amount);
            FluidRenderer.renderFluidBox(
                upperFluid.getFluid(),
                amount,
                2f / 16f, 0.5f, 2f / 16f,
                14f / 16f, yMax, 14f / 16f,
                bufferSource, poseStack, packedLight, true, false
            );
        }
        var lowerFluid = blockEntity.lowerTank().getFluidInTank(0);
        if(!lowerFluid.isEmpty()) {
            var amount = lowerFluid.getAmount();
            var yMin = 0.25f + 0.00025f * (1000 - amount);
            FluidRenderer.renderFluidBox(
                lowerFluid.getFluid(),
                amount,
                2f / 16f, yMin, 2f / 16f,
                14f / 16f, 0.5f, 14f / 16f,
                bufferSource, poseStack, packedLight, true, false
            );
        }
    }
}
