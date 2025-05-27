package systems.thedawn.espresso.block.sieve;

import java.util.ArrayList;
import java.util.List;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import net.createmod.catnip.math.VecHelper;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.EspressoRecipeTypes;
import systems.thedawn.espresso.EspressoTags;
import systems.thedawn.espresso.recipe.FilterCondition;
import systems.thedawn.espresso.recipe.SieveRecipe;
import systems.thedawn.espresso.recipe.SieveRecipeInput;
import systems.thedawn.espresso.util.ItemHandlerListView;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// sieve can hold fluids and/or items
// sieve can (must?) hold a filter
// sieve may require pressing with a mechanical press
// sieve recipes may process passively

public class SieveBlockEntity extends SmartBlockEntity {
    private static final String INPUT_INV = "InputInv";
    private static final String FILTER_INV = "FilterInv";
    private static final String UPPER_OUTPUT_INV = "UpperOutputInv";
    private static final String LOWER_OUTPUT_INV = "LowerOutputInv";
    private static final String REMAINING_TIME = "RemainingTime";
    private static final String OUTPUT_BUFFER = "OutputBuffer";

    // behaviors, set in the parent ctor
    private FilteringBehaviour recipeFilter;
    private SmartFluidTankBehaviour upperFluidTank;
    private SmartFluidTankBehaviour lowerFluidTank;
    private DirectBeltInputBehaviour beltInput;

    // inventories
    private final SmartInventory inputInventory;
    private final SmartInventory filterInventory;
    private final SmartInventory upperOutputInventory;
    private final SmartInventory lowerOutputInventory;
    private final IItemHandler upperInventories;

    /**
     * A buffer to store remainder outputs that can't be placed in the upper output inventory.
     */
    private final List<ItemStack> outputBuffer = new ArrayList<>();

    /**
     * Maximum size of the output buffer. The sieve will not process items if the buffer becomes full.
     */
    private static final int MAX_OUTPUT_BUFFER_SIZE = 16;

    /**
     * The remaining processing time for the current recipe.
     */
    private int timeRemaining = -1;

    // transient data - not serialized
    private transient @Nullable SieveRecipe currentRecipe;
    private transient boolean contentsChanged;

    public SieveBlockEntity(BlockPos pos, BlockState state) {
        super(EspressoBlockEntityTypes.SIEVE.value(), pos, state);
        this.inputInventory = new SmartInventory(3, this)
            .whenContentsChanged(slot -> this.contentsChanged = true)
            .allowInsertion()
            .allowExtraction();
        this.filterInventory = filterInventory(this)
            .whenContentsChanged(slot -> this.contentsChanged = true)
            .allowInsertion()
            .allowExtraction();
        this.upperOutputInventory = new SmartInventory(3, this)
            .whenContentsChanged(slot -> this.contentsChanged = true)
            .forbidInsertion()
            .allowExtraction();
        this.lowerOutputInventory = new SmartInventory(1, this)
            .whenContentsChanged(slot -> this.contentsChanged = true)
            .forbidInsertion()
            .allowExtraction();
        this.upperInventories = new CombinedInvWrapper(this.inputInventory, this.upperOutputInventory);
        System.out.println("Sieve constructed");
    }

    private static SmartInventory filterInventory(SieveBlockEntity be) {
        return new SmartInventory(1, be, 1, false) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return super.isItemValid(slot, stack) &&
                    (stack.is(EspressoTags.COARSE_FILTERS) || stack.is(EspressoTags.FINE_FILTERS));
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if(!this.isItemValid(slot, stack)) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public IItemHandler upperInventory() {
        return this.upperInventories;
    }

    public IItemHandler lowerInventory() {
        return this.lowerOutputInventory;
    }

    public IFluidHandler upperTank() {
        return this.upperFluidTank.getCapability();
    }

    public IFluidHandler lowerTank() {
        return this.lowerFluidTank.getCapability();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.recipeFilter = new FilteringBehaviour(this, new SieveFilterSlot())
            .forRecipes()
            .withCallback(stack -> this.contentsChanged = true);
        this.upperFluidTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, false)
            .whenFluidUpdates(() -> this.contentsChanged = true);
        this.lowerFluidTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, 1000, false)
            .whenFluidUpdates(() -> this.contentsChanged = true);
        this.beltInput = new DirectBeltInputBehaviour(this);
        behaviours.add(this.recipeFilter);
        behaviours.add(this.upperFluidTank);
        behaviours.add(this.lowerFluidTank);
        behaviours.add(this.beltInput);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent ev) {
        ev.registerBlockEntity(Capabilities.FluidHandler.BLOCK, EspressoBlockEntityTypes.SIEVE.value(), SieveBlockEntity::getFluidCapability);
        ev.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, context) -> {
            if(blockEntity instanceof SieveBlockEntity sieve) {
                return sieve.getItemCapability(state, context);
            }
            return null;
        }, EspressoBlocks.SIEVE.value());
    }

    private IFluidHandler getFluidCapability(@Nullable Direction dir) {
        if(dir == Direction.DOWN) {
            return this.lowerFluidTank.getCapability();
        }
        return this.upperFluidTank.getCapability();
    }

    private IItemHandler getItemCapability(BlockState state, @Nullable Direction dir) {
        if(dir == Direction.DOWN) {
            return this.lowerOutputInventory;
        }
        if(dir != null && SieveBlock.hasFilterEntry(state, dir)) {
            return this.filterInventory;
        }
        return this.upperInventories;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.timeRemaining > 0) {
            this.timeRemaining--;
        }
    }

    @Override
    public void lazyTick() {
        if(this.timeRemaining == 0) {
            this.setRecipe();
            if(this.currentRecipe != null && this.outputBuffer.size() < MAX_OUTPUT_BUFFER_SIZE) {
                if(this.tryAcceptOutputs(true)) {
                    this.tryAcceptOutputs(false);
                    this.shrinkInputs();
                    var filterItem = this.filterInventory.getStackInSlot(0);
                    if(filterItem.isDamageableItem()) {
                        filterItem.setDamageValue(filterItem.getDamageValue() + 1);
                    }
                }
            }
            this.timeRemaining = -1;
        }
        if(this.contentsChanged) {
            this.pullOutputBuffer();
            // verify any recipe is still valid
            this.setRecipe();
            if(this.timeRemaining < 0 && this.currentRecipe != null) {
                this.timeRemaining = this.currentRecipe.duration();
            }
            var filterType = this.getFilterType();
            if(filterType != this.getBlockState().getValue(SieveBlock.FILTER)) {
                if(this.level != null && !this.level.isClientSide()) {
                    this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SieveBlock.FILTER, filterType), 2);
                }
            }
            this.contentsChanged = false;
        }
    }

    private void setRecipe() {
        if(this.level != null && !level.isClientSide()) {
            if(this.hasBrokenFilter()) {
                // no recipes can be performed with a broken filter
                this.currentRecipe = null;
            } else {
                var input = new SieveRecipeInput(
                    new ItemHandlerListView(this.upperInventories),
                    this.upperFluidTank.getCapability().getFluidInTank(0),
                    false,
                    this.getFilterType()
                );
                // recalculate recipe if current recipe no longer applies
                if(this.currentRecipe == null || !this.currentRecipe.matches(input, this.level)) {
                    this.currentRecipe = null;
                    var recipeCandidates = this.level.getRecipeManager()
                        .getRecipesFor(EspressoRecipeTypes.SIEVING.value(), input, this.level);
                    for(var recipeHolder : recipeCandidates) {
                        if(this.acceptableRecipe(recipeHolder.value())) {
                            this.currentRecipe = recipeHolder.value();
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean acceptableRecipe(SieveRecipe recipe) {
        var filter = this.recipeFilter;
        if(filter.test(recipe.resultItem())) {
            return true;
        }
        for(var stack : recipe.remainingItems()) {
            if(filter.test(stack)) {
                return true;
            }
        }
        return filter.test(recipe.resultFluid());
    }

    private boolean hasBrokenFilter() {
        var filterItem = this.filterInventory.getStackInSlot(0);
        var damage = filterItem.getDamageValue();
        return damage > 0 && damage >= filterItem.getMaxDamage();
    }

    private FilterCondition getFilterType() {
        var filterItem = this.filterInventory.getStackInSlot(0);
        if(filterItem.isEmpty()) {
            return FilterCondition.NONE;
        }
        if(filterItem.is(EspressoTags.FINE_FILTERS)) {
            return FilterCondition.FINE;
        }
        if(filterItem.is(EspressoTags.COARSE_FILTERS)) {
            return FilterCondition.COARSE;
        }
        return FilterCondition.NONE;
    }

    private boolean tryAcceptOutputs(boolean simulate) {
        if(this.currentRecipe == null) {
            throw new IllegalStateException("Cannot accept outputs of an empty recipe");
        }

        var accepted = true;
        // insert item output
        var output = this.currentRecipe.resultItem();
        if(!output.isEmpty()) {
            this.lowerOutputInventory.allowInsertion();
            var remainingOutput = this.lowerOutputInventory.insertItem(0, output, simulate);
            this.lowerOutputInventory.forbidInsertion();
            if(!remainingOutput.isEmpty()) {
                if(!simulate) {
                    LogUtils.getLogger().warn("Sieve voided item output: " + remainingOutput);
                }
                accepted = false;
            }
        }
        // insert fluid output
        var outputFluid = this.currentRecipe.resultFluid();
        if(!outputFluid.isEmpty()) {
            this.lowerFluidTank.allowInsertion();
            var filledAmount = this.lowerFluidTank.getCapability().fill(outputFluid, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
            this.lowerFluidTank.forbidInsertion();
            if(filledAmount < outputFluid.getAmount()) {
                if(!simulate) {
                    LogUtils.getLogger().warn("Sieve voided fluid output: " + (outputFluid.getAmount() - filledAmount));
                }
                accepted = false;
            }
        }
        // insert remaining items
        var remainders = this.currentRecipe.remainingItems();
        for(var stack : remainders) {
            this.upperOutputInventory.allowInsertion();
            var leftOver = ItemHandlerHelper.insertItemStacked(this.upperOutputInventory, stack, simulate);
            this.upperOutputInventory.forbidInsertion();
            if(!leftOver.isEmpty()) {
                if(!simulate) {
                    this.outputBuffer.add(leftOver);
                }
                accepted = false;
            }
        }

        return accepted;
    }

    private void shrinkInputs() {
        if(this.currentRecipe == null) {
            throw new IllegalStateException("Cannot shrink inputs of an empty recipe");
        }

        var consumedInputs = this.currentRecipe.getMatchedInputItems(new ItemHandlerListView(this.upperInventories));
        var slots = this.upperInventories.getSlots();
        for(var consumedStack : consumedInputs) {
            for(var slot = 0; slot < slots; slot++) {
                var stack = this.upperInventories.getStackInSlot(slot);
                if(ItemStack.isSameItemSameComponents(consumedStack, stack)) {
                    this.upperInventories.extractItem(slot, 1, false);
                    break;
                }
            }
        }

        var consumedFluidAmount = this.currentRecipe.consumedFluidAmount();
        this.upperFluidTank.getCapability().drain(consumedFluidAmount, IFluidHandler.FluidAction.EXECUTE);
    }

    private void pullOutputBuffer() {
        while(!this.outputBuffer.isEmpty()) {
            var stack = this.outputBuffer.removeLast();
            stack = ItemHandlerHelper.insertItemStacked(this.upperInventories, stack, false);
            if(!stack.isEmpty()) {
                this.outputBuffer.add(stack);
                break;
            }
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.inputInventory.deserializeNBT(registries, tag.getCompound(INPUT_INV));
        this.filterInventory.deserializeNBT(registries, tag.getCompound(FILTER_INV));
        this.upperOutputInventory.deserializeNBT(registries, tag.getCompound(UPPER_OUTPUT_INV));
        this.lowerOutputInventory.deserializeNBT(registries, tag.getCompound(LOWER_OUTPUT_INV));
        this.timeRemaining = tag.getInt(REMAINING_TIME);
        this.outputBuffer.clear();
        var bufferTag = tag.get(OUTPUT_BUFFER);
        if(bufferTag != null) {
            var ops = registries.createSerializationContext(NbtOps.INSTANCE);
            this.outputBuffer.addAll(ItemStack.CODEC.listOf().parse(ops, bufferTag).resultOrPartial().orElse(List.of()));
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put(INPUT_INV, this.inputInventory.serializeNBT(registries));
        tag.put(FILTER_INV, this.filterInventory.serializeNBT(registries));
        tag.put(UPPER_OUTPUT_INV, this.upperOutputInventory.serializeNBT(registries));
        tag.put(LOWER_OUTPUT_INV, this.lowerOutputInventory.serializeNBT(registries));
        tag.putInt(REMAINING_TIME, this.timeRemaining);
        if(!this.outputBuffer.isEmpty()) {
            var ops = registries.createSerializationContext(NbtOps.INSTANCE);
            tag.put(OUTPUT_BUFFER, ItemStack.CODEC.listOf().encodeStart(ops, this.outputBuffer).getOrThrow());
        }
    }

    private static class SieveFilterSlot extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16.125);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal() && !SieveBlock.hasFilterEntry(state, direction);
        }
    }
}
