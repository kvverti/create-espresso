package systems.thedawn.espresso.block.sieve;

/**
 * Extension interface that allows sieves to notify presses above them.
 */
public interface PressingBehaviorExtension {
    void espresso$setSieveRecipe(boolean start);

    boolean espresso$isOrWasPressingSieve();
}
