package systems.thedawn.espresso.recipe;

import net.minecraft.util.StringRepresentable;

public enum FilterCondition implements StringRepresentable {
    NONE,
    COARSE,
    FINE;

    @Override
    public String getSerializedName() {
        return switch(this) {
            case NONE -> "none";
            case COARSE -> "coarse";
            case FINE -> "fine";
        };
    }
}
