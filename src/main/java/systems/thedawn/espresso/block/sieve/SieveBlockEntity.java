package systems.thedawn.espresso.block.sieve;

import java.util.List;

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
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoBlockEntityTypes;
import systems.thedawn.espresso.EspressoBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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

    private FilteringBehaviour recipeFilter;
    private SmartFluidTankBehaviour upperFluidTank;
    private SmartFluidTankBehaviour lowerFluidTank;
    private DirectBeltInputBehaviour beltInput;

    private final SmartInventory inputInventory;
    private final SmartInventory filterInventory;
    private final SmartInventory upperOutputInventory;
    private final SmartInventory lowerOutputInventory;
    private final IItemHandler upperInventories;

    public SieveBlockEntity(BlockPos pos, BlockState state) {
        super(EspressoBlockEntityTypes.SIEVE.value(), pos, state);
        this.inputInventory = new SmartInventory(3, this)
            .allowInsertion()
            .allowExtraction();
        this.filterInventory = new SmartInventory(1, this, 1, false)
            .allowInsertion()
            .allowExtraction();
        this.upperOutputInventory = new SmartInventory(3, this)
            .forbidInsertion()
            .allowExtraction();
        this.lowerOutputInventory = new SmartInventory(3, this)
            .forbidInsertion()
            .allowExtraction();
        this.upperInventories = new CombinedInvWrapper(this.inputInventory, this.upperOutputInventory);
    }

    public IItemHandler upperInventory() {
        return this.upperInventories;
    }

    public IFluidHandler upperTank() {
        return this.upperFluidTank.getCapability();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.recipeFilter = new FilteringBehaviour(this, new SieveFilterSlot())
            .forRecipes();
        this.upperFluidTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, false);
        this.lowerFluidTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, 1000, false);
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
        var facing = state.getValue(SieveBlock.FACING);
        if(dir == facing) {
            return this.filterInventory;
        }
        return this.upperInventories;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.inputInventory.deserializeNBT(registries, tag.getCompound(INPUT_INV));
        this.filterInventory.deserializeNBT(registries, tag.getCompound(FILTER_INV));
        this.upperOutputInventory.deserializeNBT(registries, tag.getCompound(UPPER_OUTPUT_INV));
        this.lowerOutputInventory.deserializeNBT(registries, tag.getCompound(LOWER_OUTPUT_INV));
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put(INPUT_INV, this.inputInventory.serializeNBT(registries));
        tag.put(FILTER_INV, this.filterInventory.serializeNBT(registries));
        tag.put(UPPER_OUTPUT_INV, this.upperOutputInventory.serializeNBT(registries));
        tag.put(LOWER_OUTPUT_INV, this.lowerOutputInventory.serializeNBT(registries));
    }

    private static class SieveFilterSlot extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16.25);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal() && state.getValue(SieveBlock.FACING) != direction;
        }
    }
}
