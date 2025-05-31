package systems.thedawn.espresso.datagen;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.block.DrinkBaseBlock;
import systems.thedawn.espresso.block.CoffeePlantBlock;
import systems.thedawn.espresso.block.sieve.SieveBlock;

import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Block;

public class EspressoBlockStateProvider extends BlockStateProvider {
    public EspressoBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Espresso.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.getVariantBuilder(EspressoBlocks.COFFEE_PLANT.value())
            .forAllStates(blockState -> {
                var age = blockState.getValue(CoffeePlantBlock.AGE);
                var stage_name = "block/coffee_plant_age_" + age;
                var model = this.models().cross(stage_name, this.modLoc(stage_name)).renderType("cutout_mipped");
                return ConfiguredModel.builder().modelFile(model).build();
            });
        this.getVariantBuilder(EspressoBlocks.GROWN_COFFEE_PLANT.value())
            .forAllStates(blockState -> {
                var age = 1 + CoffeePlantBlock.MAX_AGE + blockState.getValue(CoffeePlantBlock.AGE);
                var stage_name = "block/coffee_plant_age_" + age;
                var model = this.models().cross(stage_name, this.modLoc(stage_name)).renderType("cutout_mipped");
                return ConfiguredModel.builder().modelFile(model).build();
            });

        var coffee_bricks = this.modLoc("block/coffee_bricks");
        this.simpleBlock(EspressoBlocks.COFFEE_BRICKS.value());
        this.slabBlock(EspressoBlocks.COFFEE_BRICK_SLAB.value(), coffee_bricks, coffee_bricks);
        this.stairsBlock(EspressoBlocks.COFFEE_BRICK_STAIRS.value(), coffee_bricks);

        this.registerDrinkHolderModels(EspressoBlocks.COFFEE_MUG, "coffee_mug");
        this.registerDrinkHolderModels(EspressoBlocks.FILLED_COFFEE_MUG, "filled_mug");
        this.simpleBlock(EspressoBlocks.TALL_GLASS.value(), this.models().getExistingFile(this.modLoc("block/tall_glass")));

        this.simpleBlock(EspressoBlocks.STEEPER.value(), this.models().getExistingFile(this.modLoc("block/steeper")));

        this.registerSieveModels();
        this.registerFluidModels();
    }

    private void registerDrinkHolderModels(Holder<Block> block, String baseName) {
        var rightModel = this.models().getExistingFile(this.modLoc("block/" + baseName + "_right"));
        var leftModel = this.models().getExistingFile(this.modLoc("block/" + baseName + "_left"));
        this.getVariantBuilder(block.value())
            .forAllStates(blockState -> {
                var handedness = blockState.getValue(DrinkBaseBlock.CHIRALITY);
                var model = handedness == HumanoidArm.LEFT ? leftModel : rightModel;
                var direction = blockState.getValue(DrinkBaseBlock.FACING);
                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(direction.get2DDataValue() * 90)
                    .build();
            });
    }

    private void registerSieveModels() {
        var template = this.models().getExistingFile(this.modLoc("block/sieve"));
        this.getVariantBuilder(EspressoBlocks.SIEVE.value())
            .forAllStates(blockState -> {
                var filterId = blockState.getValue(SieveBlock.FILTER).getSerializedName();
                var rotation = blockState.getValue(SieveBlock.AXIS).choose(90, 0, 0);
                var model = this.models().getBuilder("block/sieve_" + filterId)
                    .parent(template)
                    .texture("grate", "block/sieve_filter_" + filterId);
                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(rotation)
                    .build();
            });
    }

    private void registerFluidModels() {
        var translucentWater = this.models()
            .getBuilder("block/translucent_water")
            .renderType("translucent")
            .texture("particle", this.mcLoc("block/water_still"));
        var configuredModel = ConfiguredModel.builder().modelFile(translucentWater).build();

        this.getVariantBuilder(EspressoBlocks.HOT_WATER.value())
            .partialState()
            .setModels(configuredModel);
    }
}
