package net.scottishfrogg.cutenames.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.scottishfrogg.cutenames.CuteMod;
import net.scottishfrogg.cutenames.client.CuteVariant;
import net.scottishfrogg.cutenames.client.CuteVariants;
import net.scottishfrogg.cutenames.client.jem.JemLayout;
import net.scottishfrogg.cutenames.client.jem.JemLoader;
import net.scottishfrogg.cutenames.client.jem.JemRig;
import net.scottishfrogg.cutenames.client.model.CuteWolfModel;

import java.util.HashMap;
import java.util.Map;

public class CuteWolfRenderer extends WolfRenderer {
    private final WolfModel<Wolf> vanillaModel;
    private final Map<String, WolfModel<Wolf>> customModels = new HashMap<>();
    public CuteWolfRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.vanillaModel = this.model;
        ModelPart vanillaRoot = context.bakeLayer(ModelLayers.WOLF);
        for (CuteVariant variant : CuteVariants.forEntity(EntityType.WOLF)) {
            if (!variant.hasModel()) {
                continue;
            }
            try {
                JemRig rig = JemLoader.load(context.getResourceManager(), variant.modelLocation(), JemLayout.WOLF, vanillaRoot);
                customModels.put(variant.id(), new CuteWolfModel(rig));
            } catch (Exception e) {
                CuteMod.LOGGER.error("[Cute Names] Could not build model '{}' for '{}', falling back to the vanilla wolf model", variant.model(), variant.id(), e);
            }
        }
        for (int i = 0; i < this.layers.size(); i++) {
            RenderLayer<Wolf, WolfModel<Wolf>> layer = this.layers.get(i);
            if (layer instanceof WolfCollarLayer) {
                this.layers.set(i, new GatedLayer<Wolf, WolfModel<Wolf>>(this, layer, wolf -> !hidesAddons(wolf)));
            }
        }
    }
    private boolean hidesAddons(Wolf wolf) {
        CuteVariant variant = CuteVariants.match(wolf);
        return variant != null && variant.hideAddons() && customModels.containsKey(variant.id());
    }
    @Override
    public ResourceLocation getTextureLocation(Wolf wolf) {
        CuteVariant variant = CuteVariants.match(wolf);
        return variant != null ? variant.texture() : super.getTextureLocation(wolf);
    }
    @Override
    public void render(Wolf wolf, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        CuteVariant variant = CuteVariants.match(wolf);
        WolfModel<Wolf> custom = variant != null ? customModels.get(variant.id()) : null;
        this.model = custom != null ? custom : vanillaModel;
        super.render(wolf, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
