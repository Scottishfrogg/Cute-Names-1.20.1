package net.scottishfrogg.cutenames.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.scottishfrogg.cutenames.CuteMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CuteVariants {
    public static final List<CuteVariant> ALL = List.of(
            new CuteVariant("technoblade", EntityType.PIG, List.of("technoblade"), tex("technoblade"), "technoblade", false),
            new CuteVariant("grim", EntityType.WOLF, List.of("grim"), tex("grim"), "grim", true),
            new CuteVariant("sunny", EntityType.WOLF, List.of("sunny"), tex("sunny"), null, false),
            new CuteVariant("fatfat", EntityType.CAT, List.of("fatfat", "fat fat"), tex("fat_fat"), "norwegian_forest_cat", true),
            new CuteVariant("stripers", EntityType.CAT, List.of("stripers"), tex("stripers"), null, false),
            new CuteVariant("opal", EntityType.CAT, List.of("opal"), tex("opal"), null, false),
            new CuteVariant("zozu", EntityType.CAT, List.of("zozu"), tex("zozu"), null, false),
            new CuteVariant("the_incredible_sculk", EntityType.WARDEN, List.of("the incredible sculk"), tex("the_incredible_sculk"), null, false)
    );
    private static final Map<String, CuteVariant> BY_NAME = new HashMap<>();
    static {
        for (CuteVariant variant : ALL) {
            for (String name : variant.names()) {
                BY_NAME.put(normalize(name), variant);
            }
        }
    }
    private CuteVariants() {
    }
    private static ResourceLocation tex(String file) {
        return new ResourceLocation(CuteMod.MOD_ID, "textures/entity/" + file + ".png");
    }
    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
    public static List<CuteVariant> forEntity(EntityType<?> type) {
        List<CuteVariant> out = new ArrayList<>();
        for (CuteVariant variant : ALL) {
            if (variant.entityType() == type) {
                out.add(variant);
            }
        }
        return out;
    }
    public static CuteVariant match(Entity entity) {
        if (!entity.hasCustomName()) {
            return null;
        }
        Component name = entity.getCustomName();
        if (name == null) {
            return null;
        }
        CuteVariant variant = BY_NAME.get(normalize(name.getString()));
        return variant != null && variant.entityType() == entity.getType() ? variant : null;
    }
}
