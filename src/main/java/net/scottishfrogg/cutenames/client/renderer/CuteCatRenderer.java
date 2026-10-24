package net.scottishfrogg.cutenames.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.scottishfrogg.cutenames.CuteMod;
import net.scottishfrogg.cutenames.client.CuteVariant;
import net.scottishfrogg.cutenames.client.CuteVariants;
import net.scottishfrogg.cutenames.client.jem.JemLayout;
import net.scottishfrogg.cutenames.client.jem.JemLoader;
import net.scottishfrogg.cutenames.client.jem.JemRig;
import net.scottishfrogg.cutenames.client.model.CuteCatModel;

import java.util.HashMap;
import java.util.Map;

public class CuteCatRenderer extends CatRenderer {
    private final CatModel<Cat> vanillaModel;
    private final Map<String, CatModel<Cat>> customModels = new HashMap<>();
    public CuteCatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.vanillaModel = this.model;
        ModelPart vanillaRoot = context.bakeLayer(ModelLayers.CAT);
        for (CuteVariant variant : CuteVariants.forEntity(EntityType.CAT)) {
            if (!variant.hasModel()) {
                continue;
            }
            try {
                JemRig rig = JemLoader.load(context.getResourceManager(), variant.modelLocation(), JemLayout.CAT, vanillaRoot);
                customModels.put(variant.id(), new CuteCatModel(rig));
            } catch (Exception e) {
                CuteMod.LOGGER.error("[Cute Names] Could not build model '{}' for '{}', falling back to the vanilla cat model", variant.model(), variant.id(), e);
            }
        }
        for (int i = 0; i < this.layers.size(); i++) {
            RenderLayer<Cat, CatModel<Cat>> layer = this.layers.get(i);
            if (layer instanceof CatCollarLayer) {
                this.layers.set(i, new GatedLayer<Cat, CatModel<Cat>>(this, layer, cat -> !hidesAddons(cat)));
            }
        }
    }
    private boolean hidesAddons(Cat cat) {
        CuteVariant variant = CuteVariants.match(cat);
        return variant != null && variant.hideAddons() && customModels.containsKey(variant.id());
    }
    @Override
    public ResourceLocation getTextureLocation(Cat cat) {
        CuteVariant variant = CuteVariants.match(cat);
        return variant != null ? variant.texture() : super.getTextureLocation(cat);
    }
    @Override
    public void render(Cat cat, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        CuteVariant variant = CuteVariants.match(cat);
        CatModel<Cat> custom = variant != null ? customModels.get(variant.id()) : null;
        this.model = custom != null ? custom : vanillaModel;
        super.render(cat, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
