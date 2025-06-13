package systems.thedawn.espresso.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import org.joml.Quaternionf;
import systems.thedawn.espresso.block.sieve.SieveBlockEntity;
import systems.thedawn.espresso.util.ItemHandlerUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
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
        var vertexConsumer = bufferSource.getBuffer(RenderType.TRANSLUCENT);
        var level = blockEntity.getLevel();
        var pos = blockEntity.getBlockPos();
        if(level != null) {
            var upperFluid = blockEntity.upperTank().getFluidInTank(0);
            if(!upperFluid.isEmpty()) {
                var texture = FluidSpriteCache.getFluidSprites(level, pos, upperFluid.getFluid().defaultFluidState())[0];
                var color = IClientFluidTypeExtensions.of(upperFluid.getFluid()).getTintColor(upperFluid);
                var yMax = 0.75f - 0.00025f * (1000 - upperFluid.getAmount());
                RenderUtil.renderFace(vertexConsumer, poseStack.last().pose(), texture, 2f / 16f, 2f / 16f, 14f / 16f, 14f / 16f, yMax, Direction.UP, color, packedLight, packedOverlay);
                RenderUtil.renderFace(vertexConsumer, poseStack.last().pose(), texture, 2f / 16f, 2f / 16f, 14f / 16f, 14f / 16f, 0.501f, Direction.DOWN, color, packedLight, packedOverlay);
            }
            var lowerFluid = blockEntity.lowerTank().getFluidInTank(0);
            if(!lowerFluid.isEmpty()) {
                var texture = FluidSpriteCache.getFluidSprites(level, pos, lowerFluid.getFluid().defaultFluidState())[0];
                var color = IClientFluidTypeExtensions.of(lowerFluid.getFluid()).getTintColor(lowerFluid);
                var yMin = 0.25f + 0.00025f * (1000 - lowerFluid.getAmount());
                RenderUtil.renderFace(vertexConsumer, poseStack.last().pose(), texture, 2f / 16f, 2f / 16f, 14f / 16f, 14f / 16f, 0.499f, Direction.UP, color, packedLight, packedOverlay);
                RenderUtil.renderFace(vertexConsumer, poseStack.last().pose(), texture, 2f / 16f, 2f / 16f, 14f / 16f, 14f / 16f, yMin, Direction.DOWN, color, packedLight, packedOverlay);
            }
        }
    }
}
