package systems.thedawn.espresso.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import systems.thedawn.espresso.block.steeper.SteeperBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SteeperBlockEntityRenderer implements BlockEntityRenderer<SteeperBlockEntity> {

    public SteeperBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(SteeperBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var fluid = blockEntity.getFilledFluid();
        if(!fluid.isEmpty()) {
            // render fluid inside
            FluidRenderer.renderFluidBox(
                fluid.getFluid(),
                250,
                0.25f, 0f, 0.25f,
                0.75f, 0.5f, 0.75f,
                bufferSource, poseStack, packedLight, true, false);
        }
    }
}
