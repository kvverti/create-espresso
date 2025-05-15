package systems.thedawn.espresso.datagen;

import java.util.concurrent.CompletableFuture;

import net.neoforged.neoforge.common.data.ExistingFileHelper;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoFluids;
import systems.thedawn.espresso.EspressoTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

public class EspressoFluidTagsProvider extends FluidTagsProvider {
    public EspressoFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, provider, Espresso.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EspressoTags.STEEPER_ENABLED_FLUIDS)
            .addTag(FluidTags.WATER)
            .add(EspressoFluids.SOURCE_HOT_WATER.value());
    }
}
