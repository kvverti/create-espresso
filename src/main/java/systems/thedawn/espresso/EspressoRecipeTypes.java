package systems.thedawn.espresso;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.thedawn.espresso.recipe.DrinkLevelingRecipe;
import systems.thedawn.espresso.recipe.DrinkModificationRecipe;
import systems.thedawn.espresso.recipe.SieveRecipe;
import systems.thedawn.espresso.recipe.SteepingRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class EspressoRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Espresso.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DrinkLevelingRecipe>> DRINK_LEVEL =
        RECIPE_TYPES.register("level_drink", () -> RecipeType.simple(Espresso.modLoc("level_drink")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<DrinkModificationRecipe>> DRINK_MODIFY =
        RECIPE_TYPES.register("modify_drink", () -> RecipeType.simple(Espresso.modLoc("modify_drink")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SteepingRecipe>> STEEPING =
        RECIPE_TYPES.register("steeping", () -> RecipeType.simple(Espresso.modLoc("steeping")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SieveRecipe>> SIEVING =
        RECIPE_TYPES.register("sieving", () -> RecipeType.simple(Espresso.modLoc("sieving")));

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Espresso.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, DrinkLevelingRecipe.Serializer> DRINK_LEVEL_SERIALIZER =
        RECIPE_SERIALIZERS.register("level_drink", DrinkLevelingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, DrinkModificationRecipe.Serializer> DRINK_MODIFY_SERIALIZER =
        RECIPE_SERIALIZERS.register("modify_drink", DrinkModificationRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, SteepingRecipe.Serializer> STEEPING_SERIALIZER =
        RECIPE_SERIALIZERS.register("steeping", SteepingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, SieveRecipe.Serializer> SIEVING_SERIALIZER =
        RECIPE_SERIALIZERS.register("sieving", SieveRecipe.Serializer::new);
}
