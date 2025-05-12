package systems.thedawn.espresso.client;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import systems.thedawn.espresso.Espresso;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class DrinkColorManager implements ResourceManagerReloadListener {
    public static final DrinkColorManager INSTANCE = new DrinkColorManager();

    public static final ResourceLocation DRINK_COLORS = ResourceLocation.fromNamespaceAndPath(Espresso.MODID, "color/drinks.json");

    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(DrinkColorResource.class, new DrinkColorResource.Serializer())
        .create();

    private DrinkColorManager() {
    }

    private DrinkColorResource drinkColors = new DrinkColorResource();

    public int getColor(ResourceLocation drinkLoc) {
        return this.drinkColors.lookup(drinkLoc);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        try(var resource = resourceManager.openAsReader(DRINK_COLORS)) {
            this.drinkColors = GSON.fromJson(resource, DrinkColorResource.class);
        } catch(IOException ex) {
            // reset drink colors
            this.drinkColors = new DrinkColorResource();
        }
    }
}
