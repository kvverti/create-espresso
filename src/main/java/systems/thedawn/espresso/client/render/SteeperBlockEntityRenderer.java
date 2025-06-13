package systems.thedawn.espresso.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import systems.thedawn.espresso.block.steeper.SteeperBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SteeperBlockEntityRenderer implements BlockEntityRenderer<SteeperBlockEntity> {

    public SteeperBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(SteeperBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var fluid = blockEntity.getFilledFluid();
        var level = blockEntity.getLevel();
        var pos = blockEntity.getBlockPos();
        if(!fluid.isEmpty() && level != null) {
            var stillSprite = FluidSpriteCache.getFluidSprites(level, pos, fluid.getFluid().defaultFluidState())[0];
            var vertexConsumer = bufferSource.getBuffer(RenderType.TRANSLUCENT);
            var color = 0xff000000 | IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
            RenderUtil.renderCuboid(vertexConsumer, poseStack.last().pose(), stillSprite, 0.25f, 0f, 0.25f, 0.75f, 0.5f, 0.75f, color, packedLight, packedOverlay);
        }
    }
}
