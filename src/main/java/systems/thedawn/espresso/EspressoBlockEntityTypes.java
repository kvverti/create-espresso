package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.block.DrinkBlockEntity;
import systems.thedawn.espresso.block.sieve.SieveBlockEntity;
import systems.thedawn.espresso.block.steeper.SteeperBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class EspressoBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Espresso.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrinkBlockEntity>> DRINK =
        BLOCK_ENTITY_TYPES.register("drink_holder", () ->
            BlockEntityType.Builder
                .of(DrinkBlockEntity::new, EspressoBlocks.COFFEE_MUG.value(), EspressoBlocks.TALL_GLASS.value())
                .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SteeperBlockEntity>> STEEPER =
        BLOCK_ENTITY_TYPES.register("steeper", () ->
            BlockEntityType.Builder
                .of(SteeperBlockEntity::new, EspressoBlocks.STEEPER.value())
                .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SieveBlockEntity>> SIEVE =
        BLOCK_ENTITY_TYPES.register("sieve", () ->
            BlockEntityType.Builder
                .of(SieveBlockEntity::new, EspressoBlocks.SIEVE.value())
                .build(null));
}
