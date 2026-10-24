package net.scottishfrogg.cutenames.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WardenRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.warden.Warden;
import net.scottishfrogg.cutenames.client.CuteVariant;
import net.scottishfrogg.cutenames.client.CuteVariants;

public class CuteWardenRenderer extends WardenRenderer {
    public CuteWardenRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public ResourceLocation getTextureLocation(Warden warden) {
        CuteVariant variant = CuteVariants.match(warden);
        return variant != null ? variant.texture() : super.getTextureLocation(warden);
    }
}
