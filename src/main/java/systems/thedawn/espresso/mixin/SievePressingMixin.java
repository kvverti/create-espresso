package systems.thedawn.espresso.mixin;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import systems.thedawn.espresso.block.sieve.PressingBehaviorExtension;
import systems.thedawn.espresso.block.sieve.SieveBlockEntity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

/**
 * Implements sieve recipe behavior for the mechanical press.
 */
@Mixin(PressingBehaviour.class)
public abstract class SievePressingMixin extends BeltProcessingBehaviour implements PressingBehaviorExtension {
    public SievePressingMixin(SmartBlockEntity be) {
        super(be);
    }

    @Shadow
    public boolean running;
    @Shadow
    public PressingBehaviour.PressingBehaviourSpecifics specifics;

    @Shadow
    public abstract void start(PressingBehaviour.Mode mode);

    @Unique
    private boolean espresso$pressingSieve;

    @Override
    public void espresso$startSieveRecipe() {
        this.espresso$pressingSieve = true;
    }

    // start pressing for a sieve recipe if a sieve has notified the press
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void espresso$startPressingForSieveRecipe(CallbackInfo info) {
        if(this.espresso$pressingSieve &&
            !this.running &&
            this.getWorld() != null &&
            !this.getWorld().isClientSide() &&
            this.specifics.getKineticSpeed() != 0f) {
            this.start(PressingBehaviour.Mode.BELT);
            info.cancel();
        }
    }

    // injected in the code that handles maximum press extent
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/press/PressingBehaviour;inWorld()Z",
            ordinal = 0
        )
    )
    private void espresso$checkSievingOnExtended(CallbackInfo info) {
        if(this.espresso$pressingSieve) {
            this.espresso$pressingSieve = false;
            var blockEntity = this.getWorld().getBlockEntity(this.getPos().below(2));
            if(blockEntity instanceof SieveBlockEntity sieve) {
                sieve.handlePress();
            }
        }
    }

    @Unique
    private static final String PRESSING_SIEVE = "CreateEspresso$PressingSieve";

    @Inject(method = "read", at = @At("HEAD"))
    private void espresso$readSieveData(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo info) {
        this.espresso$pressingSieve = compound.getBoolean(PRESSING_SIEVE);
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void espresso$writeSieveData(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo info) {
        compound.putBoolean(PRESSING_SIEVE, this.espresso$pressingSieve);
    }
}
