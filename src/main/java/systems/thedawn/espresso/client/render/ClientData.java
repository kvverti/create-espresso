package systems.thedawn.espresso.client.render;

import java.util.Collection;

import net.minecraft.client.resources.model.BakedModel;

/**
 * Block-/item-specific render data.
 *
 * @param bakedModels list of baked models to render
 * @param timestamp   timestamp of last load
 */
record ClientData(Collection<BakedModel> bakedModels, long timestamp) {
}
