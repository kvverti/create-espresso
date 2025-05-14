package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.block.CoffeeMugBlock;
import systems.thedawn.espresso.block.CoffeePlantBlock;
import systems.thedawn.espresso.block.DrinkBlock;
import systems.thedawn.espresso.block.steeper.SteeperBlock;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class EspressoBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Espresso.MODID);

    public static final DeferredBlock<LiquidBlock> HOT_WATER = BLOCKS.register("hot_water", () -> new LiquidBlock(
        EspressoFluids.SOURCE_HOT_WATER.value(),
        BlockBehaviour.Properties.of().liquid().replaceable().mapColor(MapColor.WATER)
    ));

    public static final DeferredBlock<CoffeePlantBlock> GROWN_COFFEE_PLANT;
    public static final DeferredBlock<CoffeePlantBlock> COFFEE_PLANT;

    static {
        var coffeePlantProps = BlockBehaviour.Properties.of()
            .randomTicks()
            .instabreak()
            .noOcclusion()
            .noCollission()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .sound(SoundType.CROP);
        GROWN_COFFEE_PLANT = BLOCKS.registerBlock(
            "grown_coffee_plant",
            props -> new CoffeePlantBlock(null, props),
            coffeePlantProps
        );
        COFFEE_PLANT = BLOCKS.registerBlock(
            "coffee_plant",
            props -> new CoffeePlantBlock(GROWN_COFFEE_PLANT.value(), props),
            coffeePlantProps
        );
    }

    public static final DeferredBlock<?> COFFEE_BRICKS;
    public static final DeferredBlock<StairBlock> COFFEE_BRICK_STAIRS;
    public static final DeferredBlock<SlabBlock> COFFEE_BRICK_SLAB;

    static {
        var props = BlockBehaviour.Properties.of()
            .strength(1.0f, 3.0f)
            .mapColor(DyeColor.BROWN)
            .requiresCorrectToolForDrops();
        COFFEE_BRICKS = BLOCKS.registerSimpleBlock("coffee_bricks", props);
        COFFEE_BRICK_STAIRS = BLOCKS.registerBlock("coffee_brick_stairs",
            p -> new StairBlock(COFFEE_BRICKS.value().defaultBlockState(), p), props);
        COFFEE_BRICK_SLAB = BLOCKS.registerBlock("coffee_brick_slab", SlabBlock::new, props);
    }

    public static final DeferredBlock<CoffeeMugBlock> COFFEE_MUG;
    public static final DeferredBlock<DrinkBlock> FILLED_COFFEE_MUG;
    public static final DeferredBlock<SteeperBlock> STEEPER;

    static {
        var props = BlockBehaviour.Properties.of()
            .instabreak()
            .noOcclusion()
            .isSuffocating((state, world, pos) -> false)
            .isViewBlocking((state, world, pos) -> false);
        COFFEE_MUG = BLOCKS.registerBlock("coffee_mug", CoffeeMugBlock::new, props);
        FILLED_COFFEE_MUG = BLOCKS.registerBlock("filled_coffee_mug", DrinkBlock::new, props);
        STEEPER = BLOCKS.registerBlock("steeper", SteeperBlock::new, props);
    }
}
