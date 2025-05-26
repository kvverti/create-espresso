package systems.thedawn.espresso.recipe;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;
import systems.thedawn.espresso.EspressoRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SieveRecipe implements Recipe<SieveRecipeInput> {
    private final List<Ingredient> itemIngredients;
    private final @Nullable SizedFluidIngredient fluidIngredient;
    private final ItemStack resultItem;
    private final List<ItemStack> remainingItems;
    private final FluidStack resultFluid;
    private final boolean requiresPress;
    private final FilterCondition filterCondition;
    private final int duration;

    public SieveRecipe(List<Ingredient> itemIngredients, Optional<SizedFluidIngredient> fluidIngredient, ItemStack resultItem, List<ItemStack> remainingItems, FluidStack resultFluid, boolean requiresPress, FilterCondition filterCondition, int duration) {
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient.orElse(null);
        this.resultItem = resultItem;
        this.remainingItems = remainingItems;
        this.resultFluid = resultFluid;
        this.requiresPress = requiresPress;
        this.filterCondition = filterCondition;
        this.duration = duration;
    }

    public ItemStack resultItem() {
        return this.resultItem.copy();
    }

    public List<ItemStack> remainingItems() {
        return this.remainingItems.stream().map(ItemStack::copy).toList();
    }

    public FluidStack resultFluid() {
        return this.resultFluid.copy();
    }

    public int duration() {
        return this.duration;
    }

    public int consumedFluidAmount() {
        return this.fluidIngredient != null ? this.fluidIngredient.amount() : 0;
    }

    /**
     * Get the actual items consumed by this recipe.
     *
     * @param items the items available to be consumed
     * @return a list of consumed items. One item in a given stack is consumed for each entry in the list.
     */
    public List<ItemStack> getMatchedInputItems(Collection<ItemStack> items) {
        var matches = this.matchItems(items);
        if(matches == null) {
            return List.of();
        }
        return matches;
    }

    @Override
    public boolean matches(SieveRecipeInput input, Level level) {
        return (this.fluidIngredient == null || this.fluidIngredient.test(input.fluid())) &&
            (!this.requiresPress || input.hasPress()) &&
            this.filterCondition.compareTo(input.filter()) <= 0 &&
            this.matchItems(input.items()) != null;
    }

    /**
     * Matches a collection of items against this recipe's ingredient predicates. A stack of count N can match with
     * up to N ingredient predicates.
     *
     * @param items the items to match against
     * @return the items that matched. A stack that matches N predicates will appear N times.
     */
    private @Nullable List<ItemStack> matchItems(Collection<ItemStack> items) {
        // Say we have four ingredients: two match item A, one matches either item A or item B, and one matches
        // item C. Say we also have three item stacks: a stack of 2 A, a stack of 1 B, and a stack of 1 C.
        // The goal is to find a solution to the following grid puzzle such that no two selected matches
        // are in the same row or column.
        //       A  A  B  C
        // A     x [x]
        // A    [x] x
        // A|B   x  x [x]
        // C             [x]
        if(this.itemIngredients.isEmpty()) {
            return List.of();
        }

        var duplicatedItems = new ArrayList<ItemStack>();
        int ingredientCount = this.itemIngredients.size();
        for(var stack : items) {
            // cap the number of duplicates to the number of ingredients
            var cappedCount = Math.min(stack.getCount(), ingredientCount);
            for(int i = 0; i < cappedCount; i++) {
                duplicatedItems.add(stack);
            }
        }

        var totalItemCount = duplicatedItems.size();
        var itemIndices = new int[ingredientCount]; // note: size-limited array
        Arrays.fill(itemIndices, -1);
        var ingredientIndex = 0;
        // greedily search for matches
        ingredients:
        while(ingredientIndex < ingredientCount) {
            items:
            for(int itemIndex = itemIndices[ingredientIndex] + 1; itemIndex < totalItemCount; itemIndex++) {
                // check if the item is already used
                for(var usedIndex : itemIndices) {
                    if(itemIndex == usedIndex) {
                        continue items;
                    }
                }
                if(this.itemIngredients.get(ingredientIndex).test(duplicatedItems.get(itemIndex))) {
                    itemIndices[ingredientIndex++] = itemIndex;
                    continue ingredients;
                }
            }
            // didn't find a match
            itemIndices[ingredientIndex--] = -1;
            if(ingredientIndex < 0) {
                // matching failed
                return null;
            }
        }
        // matching succeeded
        return Arrays.stream(itemIndices)
            .mapToObj(duplicatedItems::get)
            .toList();
    }

    @Override
    public ItemStack assemble(SieveRecipeInput input, HolderLookup.Provider registries) {
        return this.resultItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return this.itemIngredients.size() <= width * height;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.resultItem;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EspressoRecipeTypes.SIEVING_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return EspressoRecipeTypes.SIEVING.value();
    }

    public static final class Serializer implements RecipeSerializer<SieveRecipe> {
        public static final MapCodec<SieveRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.sizeLimitedListOf(5).optionalFieldOf("items", List.of()).forGetter(v -> v.itemIngredients),
            SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid").forGetter(v -> Optional.ofNullable(v.fluidIngredient)),
            ItemStack.CODEC.optionalFieldOf("result_item", ItemStack.EMPTY).forGetter(v -> v.resultItem),
            ItemStack.CODEC.sizeLimitedListOf(3).optionalFieldOf("remainder", List.of()).forGetter(v -> v.remainingItems),
            FluidStack.CODEC.optionalFieldOf("result_fluid", FluidStack.EMPTY).forGetter(v -> v.resultFluid),
            Codec.BOOL.optionalFieldOf("requires_press", false).forGetter(v -> v.requiresPress),
            StringRepresentable.fromEnum(FilterCondition::values).optionalFieldOf("filter", FilterCondition.NONE).forGetter(v -> v.filterCondition),
            Codec.INT.optionalFieldOf("duration", 250).forGetter(v -> v.duration)
        ).apply(inst, SieveRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SieveRecipe> STREAM_CODEC = StreamCodec.of(Serializer::writeBytes, Serializer::readBytes);

        private static void writeBytes(RegistryFriendlyByteBuf buf, SieveRecipe recipe) {
            buf.writeByte(recipe.itemIngredients.size());
            for(var ingredient : recipe.itemIngredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC).encode(buf, Optional.ofNullable(recipe.fluidIngredient));
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.resultItem);
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.remainingItems);
            FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.resultFluid);
            buf.writeBoolean(recipe.requiresPress);
            buf.writeEnum(recipe.filterCondition);
            buf.writeInt(recipe.duration);
        }

        private static SieveRecipe readBytes(RegistryFriendlyByteBuf buf) {
            var ingredientsCount = buf.readByte();
            var itemIngredients = new ArrayList<Ingredient>(ingredientsCount);
            for(var i = 0; i < ingredientsCount; i++) {
                var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                itemIngredients.add(ingredient);
            }
            var fluidIngredient = ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC).decode(buf);
            var resultItem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            var remainingItems = ItemStack.LIST_STREAM_CODEC.decode(buf);
            var resultFluid = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
            var requiresPress = buf.readBoolean();
            var filterCondition = buf.readEnum(FilterCondition.class);
            var duration = buf.readInt();
            return new SieveRecipe(List.copyOf(itemIngredients), fluidIngredient, resultItem, remainingItems, resultFluid, requiresPress, filterCondition, duration);
        }

        @Override
        public MapCodec<SieveRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SieveRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
