package systems.thedawn.espresso.block;

import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DrinkBlockEntity extends BlockEntity {
    private static final String DRINK_COMPONENT = "Drink";

    /**
     * The drink stored in this block entity. This is only expected
     * to change all at once.
     */
    private @Nullable DrinkComponent drinkData;

    public DrinkBlockEntity(BlockPos pos, BlockState blockState) {
        super(EspressoBlockEntityTypes.DRINK.value(), pos, blockState);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.drinkData = componentInput.get(EspressoDataComponentTypes.DRINK);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EspressoDataComponentTypes.DRINK, this.drinkData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var drinkComponent = tag.getCompound(DRINK_COMPONENT);
        this.drinkData = DrinkComponent.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), drinkComponent)
            .resultOrPartial()
            .orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(this.drinkData != null) {
            var drinkCompound = DrinkComponent.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.drinkData)
                .resultOrPartial();
            drinkCompound.ifPresent(compound -> tag.put(DRINK_COMPONENT, compound));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = super.getUpdateTag(registries);
        this.saveAdditional(tag, registries);
        return tag;
    }
}
