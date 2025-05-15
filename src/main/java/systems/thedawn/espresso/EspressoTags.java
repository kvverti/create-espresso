package systems.thedawn.espresso;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public final class EspressoTags {
    public static final TagKey<Item> STEEPER_ENABLED_ITEMS =
        TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "steeper_enabled"));

    public static final TagKey<Fluid> STEEPER_ENABLED_FLUIDS =
        TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "steeper_enabled"));
}
