package systems.thedawn.espresso.recipe;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoDataComponentTypes;
import systems.thedawn.espresso.EspressoRecipeTypes;
import systems.thedawn.espresso.EspressoRegistries;
import systems.thedawn.espresso.drink.BuiltinEspressoDrinks;
import systems.thedawn.espresso.drink.DrinkComponent;
import systems.thedawn.espresso.drink.DrinkModifier;

import net.minecraft.core.Holder;
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

public class DrinkModificationRecipe implements FluidInputRecipe<DrinkModificationRecipeInput> {
    private final Ingredient drinkHolder;
    private final Holder<DrinkModifier> modifier;
    private final @Nullable Ingredient appliedItem;
    private final @Nullable FluidIngredient appliedFluid;

    public DrinkModificationRecipe(Ingredient drinkHolder, Holder<DrinkModifier> modifier, @Nullable Ingredient appliedItem, @Nullable FluidIngredient appliedFluid) {
        this.drinkHolder = drinkHolder;
        this.modifier = modifier;
        this.appliedItem = appliedItem;
        this.appliedFluid = appliedFluid;
    }

    public List<ItemStack> holderStacks() {
        return Arrays.stream(this.drinkHolder.getItems())
            .flatMap(stack -> Espresso.getRegistry(EspressoRegistries.DRINKS)
                .holders()
                .filter(drinkBase -> drinkBase.getKey() != BuiltinEspressoDrinks.EMPTY)
                .map(drinkBase -> {
                    var component = DrinkComponent.initial(drinkBase);
                    var inputStack = stack.copy();
                    inputStack.set(EspressoDataComponentTypes.DRINK, component);
                    return inputStack;
                }))
            .toList();
    }

    @Nullable
    public Ingredient appliedItem() {
        return this.appliedItem;
    }

    @Nullable
    public FluidIngredient appliedFluid() {
        return this.appliedFluid;
    }

    public ItemStack modifiedResultStack(ItemStack input) {
        var component = Objects.requireNonNull(input.get(EspressoDataComponentTypes.DRINK));
        component = component.addModifier(this.modifier);
        var output = input.copy();
        output.set(EspressoDataComponentTypes.DRINK, component);
        return output;
    }

    @Override
    public boolean matches(DrinkModificationRecipeInput input, Level level) {
        // check the drink holder matches
        if(!this.drinkHolder.test(input.drinkHolder())) {
            return false;
        }
        // check that the modifier has not already been applied
        var drinkData = input.drinkHolder().get(EspressoDataComponentTypes.DRINK);
        if(drinkData == null) {
            // not a drink -- can't modify it
            return false;
        }
        if(drinkData.modifiers().stream().anyMatch(modifier -> modifier.getKey() == this.modifier.getKey())) {
            return false;
        }
        // check the applied item, if any
        if(this.appliedItem != null && input.appliedItem() != null) {
            return this.appliedItem.test(input.appliedItem());
        }
        // otherwise check the applied fluid, if any
        if(this.appliedFluid != null && input.appliedFluid() != null) {
            return this.appliedFluid.test(input.appliedFluid()) &&
                this.appliedFluid.getRequiredAmount() <= input.appliedFluid().getAmount();
        }
        return false;
    }

    @Override
    public ItemStack assemble(DrinkModificationRecipeInput input, HolderLookup.Provider registries) {
        var inputStack = input.drinkHolder();
        var component = inputStack.get(EspressoDataComponentTypes.DRINK);
        if(component != null) {
            var newComponent = component.addModifier(this.modifier);
            var outputStack = inputStack.copy();
            outputStack.set(EspressoDataComponentTypes.DRINK, newComponent);
            return outputStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assembleFromFluid(ItemStack input, FluidStack fluidInput, HolderLookup.Provider registries) {
        return this.assemble(new DrinkModificationRecipeInput(input, null, fluidInput), registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        if(this.drinkHolder.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.drinkHolder.getItems()[0];
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EspressoRecipeTypes.DRINK_MODIFY_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return EspressoRecipeTypes.DRINK_MODIFY.value();
    }

    @Override
    public int fillAmount() {
        return this.appliedFluid == null ? 0 : this.appliedFluid.getRequiredAmount();
    }

    public static class DynamicResult implements Recipe<DrinkModificationRecipeInput> {
        private final ItemStack result;

        public DynamicResult(ItemStack result) {
            this.result = result;
        }

        @Override
        public boolean matches(DrinkModificationRecipeInput input, Level level) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ItemStack assemble(DrinkModificationRecipeInput input, HolderLookup.Provider registries) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return true;
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            return this.result;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public RecipeType<?> getType() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Serializer implements RecipeSerializer<DrinkModificationRecipe> {
        public static final MapCodec<DrinkModificationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("holder").forGetter(v -> v.drinkHolder),
            DrinkModifier.CODEC.fieldOf("modifier").forGetter(v -> v.modifier),
            Ingredient.CODEC.optionalFieldOf("applied_item").forGetter(v -> Optional.ofNullable(v.appliedItem)),
            FluidIngredient.CODEC.optionalFieldOf("applied_fluid").forGetter(v -> Optional.ofNullable(v.appliedFluid))
        ).apply(inst, (holder, modifier, item, fluid) -> new DrinkModificationRecipe(holder, modifier, item.orElse(null), fluid.orElse(null))));
        public static final StreamCodec<RegistryFriendlyByteBuf, DrinkModificationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            v -> v.drinkHolder,
            DrinkModifier.STREAM_CODEC,
            v -> v.modifier,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
            v -> Optional.ofNullable(v.appliedItem),
            ByteBufCodecs.optional(FluidIngredient.STREAM_CODEC),
            v -> Optional.ofNullable(v.appliedFluid),
            (holder, modifier, item, fluid) -> new DrinkModificationRecipe(holder, modifier, item.orElse(null), fluid.orElse(null))
        );

        @Override
        public MapCodec<DrinkModificationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DrinkModificationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
