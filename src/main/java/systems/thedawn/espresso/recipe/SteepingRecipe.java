package systems.thedawn.espresso.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import systems.thedawn.espresso.EspressoRecipeTypes;
import systems.thedawn.espresso.block.steeper.SteeperBlock;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A steeping recipe. Steeping accepts a fixed amount of fluid and a variable count of
 * an item, and produces a fixed amount of fluid and an equivalent amount of another item.
 */
public class SteepingRecipe implements Recipe<SteepingRecipeInput> {
    /**
     * The steeping input fluid. The amount is ignored.
     */
    private final FluidIngredient steepingFluid;

    /**
     * The steeping item. The count is ignored and is set based on the output.
     */
    private final Ingredient steepingItem;

    /**
     * The result fluid. The amount is ignored.
     */
    private final FluidStack resultFluid;

    /**
     * The result item.
     */
    private final ItemStack resultItem;

    /**
     * The time in ticks to process the recipe.
     */
    private final int duration;

    public SteepingRecipe(FluidIngredient steepingFluid, Ingredient steepingItem, FluidStack resultFluid, ItemStack resultItem, int duration) {
        this.steepingFluid = steepingFluid;
        this.steepingItem = steepingItem;
        this.resultFluid = resultFluid;
        this.resultItem = resultItem;
        this.duration = duration;

        this.resultFluid.setAmount(SteeperBlock.FLUID_CAPACITY);
    }

    public FluidStack resultFluid() {
        return this.resultFluid;
    }

    public ItemStack resultItem() {
        return this.resultItem;
    }

    public int duration() {
        return this.duration;
    }

    @Override
    public boolean matches(SteepingRecipeInput input, Level level) {
        return this.steepingFluid.test(input.fluid()) &&
            this.steepingItem.test(input.steeped()) &&
            input.fluid().getAmount() >= SteeperBlock.FLUID_CAPACITY &&
            input.steeped().getCount() >= this.resultItem.getCount();
    }

    @Override
    public ItemStack assemble(SteepingRecipeInput input, HolderLookup.Provider registries) {
        return this.resultItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.resultItem;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EspressoRecipeTypes.STEEPING_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return EspressoRecipeTypes.STEEPING.value();
    }

    public static class Serializer implements RecipeSerializer<SteepingRecipe> {
        public static final MapCodec<SteepingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FluidIngredient.CODEC_NON_EMPTY.fieldOf("steeping_fluid").forGetter(v -> v.steepingFluid),
            Ingredient.CODEC_NONEMPTY.fieldOf("steeping_item").forGetter(v -> v.steepingItem),
            FluidStack.CODEC.fieldOf("result_fluid").forGetter(v -> v.resultFluid),
            ItemStack.CODEC.fieldOf("result_item").forGetter(v -> v.resultItem),
            Codec.INT.fieldOf("duration").forGetter(v -> v.duration)
        ).apply(inst, SteepingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SteepingRecipe> STREAM_CODEC = StreamCodec.composite(
            FluidIngredient.STREAM_CODEC,
            v -> v.steepingFluid,
            Ingredient.CONTENTS_STREAM_CODEC,
            v -> v.steepingItem,
            FluidStack.STREAM_CODEC,
            v -> v.resultFluid,
            ItemStack.STREAM_CODEC,
            v -> v.resultItem,
            ByteBufCodecs.INT,
            v -> v.duration,
            SteepingRecipe::new
        );

        @Override
        public MapCodec<SteepingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SteepingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
