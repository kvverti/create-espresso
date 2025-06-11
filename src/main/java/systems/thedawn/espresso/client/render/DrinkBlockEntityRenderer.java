package systems.thedawn.espresso.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import systems.thedawn.espresso.block.AbstractDrinkBlock;
import systems.thedawn.espresso.block.DrinkBlockEntity;
import systems.thedawn.espresso.client.model.DrinkModelManager;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;

public class DrinkBlockEntityRenderer implements BlockEntityRenderer<DrinkBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    private final ModelManager modelManager;

    public DrinkBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockRenderDispatcher = ctx.getBlockRenderDispatcher();
        this.modelManager = this.blockRenderDispatcher.getBlockModelShaper().getModelManager();
    }

    @Override
    public boolean shouldRender(DrinkBlockEntity blockEntity, Vec3 cameraPos) {
        var clientData = this.getClientData(blockEntity);
        if(clientData == null || clientData.bakedModels().isEmpty()) {
            return false;
        }
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public void render(DrinkBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var clientData = this.getClientData(blockEntity);
        var level = blockEntity.getLevel();
        if(clientData != null && level != null) {
            poseStack.pushPose();
            var handedness = blockEntity.getBlockState().getValue(AbstractDrinkBlock.CHIRALITY);
            var rotation = handedness == HumanoidArm.LEFT ? 22.5f : -22.5f;
            poseStack.rotateAround(new Quaternionf().fromAxisAngleDeg(0f, 1f, 0f, rotation), 0.5f, 0.5f, 0.5f);
            for(var bakedModel : clientData.bakedModels()) {
                this.blockRenderDispatcher
                    .getModelRenderer()
                    .tesselateBlock(
                        level,
                        bakedModel,
                        blockEntity.getBlockState(),
                        blockEntity.getBlockPos(),
                        poseStack,
                        bufferSource.getBuffer(RenderType.translucent()),
                        false,
                        level.getRandom(),
                        0,
                        packedOverlay,
                        blockEntity.getModelData(),
                        RenderType.translucent()
                    );
            }
            poseStack.popPose();
        }
    }

    private @Nullable ClientData getClientData(DrinkBlockEntity blockEntity) {
        var lastLoad = DrinkModelManager.lastLoadTimestamp();
        var clientData = (ClientData) blockEntity.getClientData();
        if(clientData != null && clientData.timestamp() == lastLoad) {
            return clientData;
        }
        var block = blockEntity.getBlockState().getBlock();
        var drink = blockEntity.drink();
        if(drink != null) {
            clientData = new ClientData(DrinkModelManager.getBlockModels(block, drink, this.modelManager), lastLoad);
            blockEntity.setClientData(clientData);
        }
        return clientData;
    }
}
