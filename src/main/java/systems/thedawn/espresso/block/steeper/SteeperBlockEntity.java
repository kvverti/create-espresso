package systems.thedawn.espresso.block.steeper;

import java.util.Optional;

import net.neoforged.neoforge.fluids.FluidStack;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.EspressoRecipeTypes;
import systems.thedawn.espresso.recipe.SteepingRecipe;
import systems.thedawn.espresso.recipe.SteepingRecipeInput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SteeperBlockEntity extends BlockEntity {
    private static final String STEEPING = "Steeping";
    private static final String STEEPING_TIME = "SteepTime";
    private static final String MAX_STEEPING_TIME = "MaxSteepTime";
    private static final int DONE_STEEPING = -1;

    /**
     * The item and fluid content of the steeper.
     */
    private SteeperComponent steepingData = SteeperComponent.empty();

    /**
     * The time spent steeping.
     */
    private int steepingTime = DONE_STEEPING;

    /**
     * The time when the contents are done steeping.
     */
    private int maxSteepingTime;

    public SteeperBlockEntity(BlockPos pos, BlockState blockState) {
        super(EspressoBlockEntityTypes.STEEPER.value(), pos, blockState);
    }

    /**
     * Updates steeping inventory and sends those updates to the client.
     * All modification of steeping data outside of ser/de should go through this method.
     *
     * @param data the new data component
     */
    private void updateSteepingData(SteeperComponent data) {
        this.steepingData = data;
        if(this.level != null && !this.level.isClientSide()) {
            var state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
        }
    }

    boolean canFillWithFluid() {
        return this.steepingData.drinkFluid().isEmpty();
    }

    void fillWithFluid(FluidStack fluid) {
        var item = this.steepingData.dregs();
        this.updateSteepingData(new SteeperComponent(fluid, item));
        this.updateRecipe();
    }

    void drainFluid() {
        var item = this.steepingData.dregs();
        this.updateSteepingData(new SteeperComponent(FluidStack.EMPTY, item));
        this.updateRecipe();
    }

    boolean canPlaceItem(ItemStack stack) {
        if(this.steepingData.dregs().isEmpty()) {
            return true;
        }
        if(stack.isEmpty()) {
            return false;
        }
        var dregs = this.steepingData.dregs();
        return dregs.getCount() < dregs.getMaxStackSize() && ItemStack.isSameItemSameComponents(dregs, stack);
    }

    void placeItem(ItemStack stack) {
        var fluid = this.steepingData.drinkFluid();
        var dregs = this.steepingData.dregs();
        if(dregs.isEmpty()) {
            dregs = stack;
        } else {
            dregs.grow(stack.getCount());
        }
        this.updateSteepingData(new SteeperComponent(fluid, dregs));
        this.updateRecipe();
    }

    boolean hasItems() {
        return !this.steepingData.dregs().isEmpty();
    }

    ItemStack takeItems() {
        var fluid = this.steepingData.drinkFluid();
        var item = this.steepingData.dregs();
        this.updateSteepingData(new SteeperComponent(fluid, ItemStack.EMPTY));
        this.updateRecipe();
        return item;
    }

    FluidStack getFilledFluid() {
        return this.steepingData.drinkFluid();
    }

    void tick() {
        if(this.steepingTime == DONE_STEEPING) {
            return;
        }
        if(this.steepingTime == this.maxSteepingTime) {
            // perform steeping recipe
            this.findRecipe()
                .ifPresent(recipe -> this.updateSteepingData(new SteeperComponent(recipe.resultFluid(), recipe.resultItem())));
            this.steepingTime = DONE_STEEPING;
        } else {
            this.steepingTime++;
        }
    }

    private Optional<SteepingRecipe> findRecipe() {
        if(this.level == null) {
            return Optional.empty();
        }
        var input = new SteepingRecipeInput(this.steepingData.drinkFluid(), this.steepingData.dregs());
        return this.level.getRecipeManager()
            .getRecipeFor(EspressoRecipeTypes.STEEPING.value(), input, this.level)
            .map(RecipeHolder::value);
    }

    private void updateRecipe() {
        this.findRecipe().ifPresent(recipe -> {
            this.steepingTime = 0;
            this.maxSteepingTime = recipe.duration();
        });
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EspressoDataComponentTypes.STEEPING, this.steepingData);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.steepingData = componentInput.getOrDefault(EspressoDataComponentTypes.STEEPING, SteeperComponent.empty());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        this.steepingData = SteeperComponent.CODEC.parse(ops, tag.getCompound(STEEPING))
            .resultOrPartial()
            .orElse(new SteeperComponent(FluidStack.EMPTY, ItemStack.EMPTY));
        this.steepingTime = tag.getInt(STEEPING_TIME);
        this.maxSteepingTime = tag.getInt(MAX_STEEPING_TIME);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        SteeperComponent.CODEC.encodeStart(ops, this.steepingData)
            .resultOrPartial()
            .ifPresent(data -> tag.put(STEEPING, data));
        tag.putInt(STEEPING_TIME, this.steepingTime);
        tag.putInt(MAX_STEEPING_TIME, this.maxSteepingTime);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = super.getUpdateTag(registries);
        SteeperComponent.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.steepingData)
            .resultOrPartial()
            .ifPresent(data -> tag.put(STEEPING, data));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }
}
