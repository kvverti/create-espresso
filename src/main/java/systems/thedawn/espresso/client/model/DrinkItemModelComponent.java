package systems.thedawn.espresso.client.model;

/**
 * Item component used to cache drink models. This is a client-only transient component;
 * it is never synchronized nor persisted.
 */
public record DrinkItemModelComponent(Object clientData) {
    public static final DrinkItemModelComponent INVALID = new DrinkItemModelComponent(false);
}
