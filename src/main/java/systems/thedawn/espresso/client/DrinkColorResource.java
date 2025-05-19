package systems.thedawn.espresso.client;

import javax.annotation.Nullable;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.resources.ResourceLocation;

public class DrinkColorResource {
    private final Object2IntOpenHashMap<ResourceLocation> colorLookup;

    public DrinkColorResource() {
        this(new Object2IntOpenHashMap<>());
    }

    public DrinkColorResource(Object2IntOpenHashMap<ResourceLocation> colorLookup) {
        this.colorLookup = colorLookup;
    }

    public int lookup(ResourceLocation drinkLoc) {
        return this.colorLookup.getOrDefault(drinkLoc, -1);
    }

    DrinkColorResource combine(DrinkColorResource other) {
        this.colorLookup.putAll(other.colorLookup);
        return this;
    }

    public static class Serializer extends TypeAdapter<DrinkColorResource> {
        private static final String DRINK = "drink";
        private static final String COLOR = "color";

        @Override
        public void write(JsonWriter out, DrinkColorResource value) throws IOException {
            out.beginArray();
            for(var entry : value.colorLookup.object2IntEntrySet()) {
                out.beginObject();
                out.name(DRINK);
                out.value(entry.getKey().toString());
                out.name(COLOR);
                out.value(Integer.toHexString(entry.getIntValue()));
                out.endObject();
            }
            out.endArray();
        }

        @Override
        public DrinkColorResource read(JsonReader in) throws IOException {
            var lookup = new Object2IntOpenHashMap<ResourceLocation>();
            in.beginArray();
            while(in.peek() == JsonToken.BEGIN_OBJECT) {
                @Nullable ResourceLocation drink = null;
                int color = -1;

                in.beginObject();
                while(in.peek() != JsonToken.END_OBJECT) {
                    switch(in.nextName()) {
                        case DRINK -> drink = ResourceLocation.tryParse(in.nextString());
                        case COLOR -> {
                            try {
                                color = Integer.parseInt(in.nextString(), 16);
                            } catch(NumberFormatException ex) {
                                LogUtils.getLogger().warn("Invalid color at " + in.getPreviousPath());
                            }
                        }
                        default -> in.skipValue();
                    }
                }
                in.endObject();

                if(drink != null) {
                    lookup.put(drink, color);
                }
            }
            in.endArray();
            return new DrinkColorResource(lookup);
        }
    }
}
