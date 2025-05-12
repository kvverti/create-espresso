package systems.thedawn.espresso.datagen;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoBlocks;
import systems.thedawn.espresso.block.CoffeeMugBlock;
import systems.thedawn.espresso.block.CoffeePlantBlock;

import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.HumanoidArm;

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

        var emptyMugRight = this.models().getExistingFile(this.modLoc("block/coffee_mug_right"));
        var emptyMugLeft = this.models().getExistingFile(this.modLoc("block/coffee_mug_left"));
        this.getVariantBuilder(EspressoBlocks.COFFEE_MUG.value())
            .forAllStates(blockState -> {
                var handedness = blockState.getValue(CoffeeMugBlock.CHIRALITY);
                var model = handedness == HumanoidArm.LEFT ? emptyMugLeft : emptyMugRight;
                var direction = blockState.getValue(CoffeeMugBlock.FACING);
                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(direction.get2DDataValue() * 90)
                    .build();
            });
        var filledMugRight = this.models().getExistingFile(this.modLoc("block/filled_mug_right"));
        var filledMugLeft = this.models().getExistingFile(this.modLoc("block/filled_mug_left"));
        this.getVariantBuilder(EspressoBlocks.FILLED_COFFEE_MUG.value())
            .forAllStates(blockState -> {
                var handedness = blockState.getValue(CoffeeMugBlock.CHIRALITY);
                var model = handedness == HumanoidArm.LEFT ? filledMugLeft : filledMugRight;
                var direction = blockState.getValue(CoffeeMugBlock.FACING);
                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(direction.get2DDataValue() * 90)
                    .build();
            });

        this.registerFluidModels();
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
