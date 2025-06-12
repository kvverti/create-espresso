package systems.thedawn.espresso.client.render;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.client.model.DrinkModelManager;
import systems.thedawn.espresso.item.DrinkItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DrinkItemRenderer extends BlockEntityWithoutLevelRenderer {
    /**
     * Cache of model data associated with an item stack. This assumes the drink component
     * does not change.
     */
    private final Map<ItemStack, ClientData> clientDataMap = new WeakHashMap<>();
    private final ItemRenderer itemRenderer;
    private final ModelManager modelManager;

    public DrinkItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.modelManager = Minecraft.getInstance().getModelManager();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        for(var bakedModel : this.getModels(stack)) {
            this.itemRenderer.render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, bakedModel);
        }
        poseStack.popPose();
    }

    private Collection<BakedModel> getModels(ItemStack stack) {
        var lastLoad = DrinkModelManager.lastLoadTimestamp();
        var clientData = clientDataMap.get(stack);
        if(clientData != null && clientData.timestamp() == lastLoad) {
            return clientData.bakedModels();
        }

        if(stack.getItem() instanceof DrinkItem drinkItem) {
            var block = drinkItem.getBlock();
            var drink = stack.get(EspressoDataComponentTypes.DRINK);
            if(drink != null) {
                var bakedModels = DrinkModelManager.getItemModels(block, drink, this.modelManager);
                clientDataMap.put(stack, new ClientData(bakedModels, lastLoad));
                return bakedModels;
            }
        }

        return List.of();
    }
}
