package systems.thedawn.espresso;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class EspressoTags {
    public static final TagKey<Block> ACCEPTS_SIEVE_OUTPUT =
        TagKey.create(Registries.BLOCK, Espresso.modLoc("accepts_sieve_output"));

    public static final TagKey<Item> STEEPER_ENABLED_ITEMS =
        TagKey.create(Registries.ITEM, Espresso.modLoc("steeper_enabled"));
    public static final TagKey<Item> COARSE_FILTERS =
        TagKey.create(Registries.ITEM, Espresso.modLoc("coarse_filters"));
    public static final TagKey<Item> FINE_FILTERS =
        TagKey.create(Registries.ITEM, Espresso.modLoc("fine_filters"));

    public static final TagKey<Fluid> STEEPER_ENABLED_FLUIDS =
        TagKey.create(Registries.FLUID, Espresso.modLoc("steeper_enabled"));
}
