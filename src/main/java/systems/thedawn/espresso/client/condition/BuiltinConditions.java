package systems.thedawn.espresso.client.condition;

import systems.thedawn.espresso.Espresso;

import net.minecraft.resources.ResourceLocation;

public class BuiltinConditions {
    public static final ResourceLocation HAS_DRINK = modLoc("has_drink");
    public static final ResourceLocation HAS_MILK = modLoc("has_milk");
    public static final ResourceLocation HAS_BUBBLES = modLoc("has_bubbles");
    public static final ResourceLocation HAS_ICE = modLoc("has_ice");
    public static final ResourceLocation IS_COFFEE = modLoc("is_coffee");

    private static ResourceLocation modLoc(String name) {
        return ResourceLocation.fromNamespaceAndPath(Espresso.MODID, name);
    }
}
