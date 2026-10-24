package net.scottishfrogg.cutenames.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.scottishfrogg.cutenames.CuteMod;

import java.util.List;

public record CuteVariant(String id, EntityType<?> entityType, List<String> names, ResourceLocation texture, String model, boolean hideAddons) {
    public boolean hasModel() {
        return model != null;
    }
    public ResourceLocation modelLocation() {
        return new ResourceLocation(CuteMod.MOD_ID, "models/entity/" + model + ".jem");
    }
}
