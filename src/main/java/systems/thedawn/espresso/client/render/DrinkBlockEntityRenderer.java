package systems.thedawn.espresso.client.render;

import java.util.Collection;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.block.DrinkBlockEntity;
import systems.thedawn.espresso.client.model.DrinkModelManager;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
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
        if(clientData == null || clientData.bakedModels.isEmpty()) {
            return false;
        }
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public void render(DrinkBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var clientData = this.getClientData(blockEntity);
        var level = blockEntity.getLevel();
        if(clientData != null && level != null) {
            for(var bakedModel : clientData.bakedModels) {
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
        }
    }

    private @Nullable ClientData getClientData(DrinkBlockEntity blockEntity) {
        var clientData = (ClientData) blockEntity.getClientData();
        if(clientData != null) {
            return clientData;
        }
        var block = blockEntity.getBlockState().getBlock();
        var drink = blockEntity.drink();
        if(drink != null) {
            clientData = new ClientData(DrinkModelManager.getBlockModels(block, drink, this.modelManager));
            blockEntity.setClientData(clientData);
        }
        return clientData;
    }

    private record ClientData(Collection<BakedModel> bakedModels) {
    }
}
