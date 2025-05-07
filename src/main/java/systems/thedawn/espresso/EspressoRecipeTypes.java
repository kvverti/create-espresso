package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.recipe.DrinkLevelingRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class EspressoRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Espresso.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DrinkLevelingRecipe>> DRINK_LEVEL =
        RECIPE_TYPES.register("level_drink", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "level_drink")));

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Espresso.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, DrinkLevelingRecipe.Serializer> DRINK_LEVEL_SERIALIZER =
        RECIPE_SERIALIZERS.register("level_drink", DrinkLevelingRecipe.Serializer::new);
}
