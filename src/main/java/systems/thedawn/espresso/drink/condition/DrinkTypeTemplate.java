package systems.thedawn.espresso.drink.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;
import systems.thedawn.espresso.drink.Drink;
import systems.thedawn.espresso.drink.DrinkComponent;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.StringRepresentable;

/**
 * Drink condition template that matches on the type of drink.
 */
public final class DrinkTypeTemplate implements ConditionTemplate<List<Drink.Type>> {
    public static final MapCodec<List<Drink.Type>> PARAMS_CODEC =
        StringRepresentable.fromEnum(Drink.Type::values).listOf().fieldOf("drink_types");

    @Override
    public boolean test(DrinkComponent drink, HolderLookup.Provider registries, List<Drink.Type> params) {
        return params.contains(drink.base().value().type());
    }

    @Override
    public MapCodec<List<Drink.Type>> paramsCodec() {
        return PARAMS_CODEC;
    }
}
