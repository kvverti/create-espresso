package systems.thedawn.espresso.client.model;

import java.util.List;

import com.mojang.serialization.Codec;

public record MultipartDrinkModel(List<MultipartEntry> entries) {
    public static final Codec<MultipartDrinkModel> CODEC =
        MultipartEntry.CODEC.listOf().xmap(MultipartDrinkModel::new, MultipartDrinkModel::entries);
}
