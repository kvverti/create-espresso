package systems.thedawn.espresso.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidStack;
import systems.thedawn.espresso.*;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Increases the base level of a drink by one, up to a maximum.
 */
public class DrinkLevelingRecipe implements FluidInputRecipe<DrinkLevelingRecipeInput> {
    /**
     * The item holding the drink.
     */
    private final Ingredient drinkHolder;

    /**
     * The drink to fill with.
     */
    private final Holder<Drink> drink;

    /**
     * The amount in mB to reduce the fluid input by.
     */
    private final int amount;

    public DrinkLevelingRecipe(Ingredient drinkHolder, Holder<Drink> drink, int amount) {
        this.drinkHolder = drinkHolder;
        this.drink = drink;
        this.amount = amount;
    }

    @Override
    public int fillAmount() {
        return this.amount;
    }

    @Override
    public boolean matches(DrinkLevelingRecipeInput input, Level level) {
        if(this.drinkHolder.test(input.drinkHolder())) {
            var heldDrink = input.drinkHolder().get(EspressoDataComponentTypes.DRINK);
            var fluidDrink = input.drinkFluid().get(EspressoDataComponentTypes.DRINK_BASE);
            if(heldDrink != null && fluidDrink != null) {
                return fluidDrink.getKey() == this.drink.getKey() &&
                    heldDrink.base().getKey() == this.drink.getKey() &&
                    heldDrink.level() != DrinkComponent.BaseLevel.TRIPLE &&
                    input.drinkFluid().getAmount() >= this.amount;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(DrinkLevelingRecipeInput input, HolderLookup.Provider registries) {
        var stack = input.drinkHolder();
        var component = stack.get(EspressoDataComponentTypes.DRINK);
        if(component != null) {
            var outputStack = stack.copy();
            outputStack.set(EspressoDataComponentTypes.DRINK, component.incrementLevel());
            return outputStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assembleFromFluid(ItemStack input, FluidStack fluidInput, HolderLookup.Provider registries) {
        return this.assemble(new DrinkLevelingRecipeInput(input, fluidInput), registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 0;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return EspressoItems.FILLED_COFFEE_MUG.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EspressoRecipeTypes.DRINK_LEVEL_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return EspressoRecipeTypes.DRINK_LEVEL.value();
    }

    public static class Serializer implements RecipeSerializer<DrinkLevelingRecipe> {
        public static final MapCodec<DrinkLevelingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("holder").forGetter(v -> v.drinkHolder),
            Drink.CODEC.fieldOf("drink").forGetter(v -> v.drink),
            Codec.INT.fieldOf("amount").forGetter(v -> v.amount)
        ).apply(inst, DrinkLevelingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, DrinkLevelingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            v -> v.drinkHolder,
            Drink.STREAM_CODEC,
            v -> v.drink,
            ByteBufCodecs.INT,
            v -> v.amount,
            DrinkLevelingRecipe::new
        );

        @Override
        public MapCodec<DrinkLevelingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DrinkLevelingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
