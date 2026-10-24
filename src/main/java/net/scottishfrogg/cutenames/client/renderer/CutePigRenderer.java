package net.scottishfrogg.cutenames.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.scottishfrogg.cutenames.CuteMod;
import net.scottishfrogg.cutenames.client.CuteVariant;
import net.scottishfrogg.cutenames.client.CuteVariants;
import net.scottishfrogg.cutenames.client.jem.JemLayout;
import net.scottishfrogg.cutenames.client.jem.JemLoader;
import net.scottishfrogg.cutenames.client.jem.JemRig;
import net.scottishfrogg.cutenames.client.model.CutePigModel;

import java.util.HashMap;
import java.util.Map;

public class CutePigRenderer extends PigRenderer {
    private final PigModel<Pig> vanillaModel;
    private final Map<String, PigModel<Pig>> customModels = new HashMap<>();
    public CutePigRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.vanillaModel = this.model;
        ModelPart vanillaRoot = context.bakeLayer(ModelLayers.PIG);
        for (CuteVariant variant : CuteVariants.forEntity(EntityType.PIG)) {
            if (!variant.hasModel()) {
                continue;
            }
            try {
                JemRig rig = JemLoader.load(context.getResourceManager(), variant.modelLocation(), JemLayout.PIG, vanillaRoot);
                customModels.put(variant.id(), new CutePigModel(rig));
            } catch (Exception e) {
                CuteMod.LOGGER.error("[Cute Names] Could not build model '{}' for '{}', falling back to the vanilla pig model", variant.model(), variant.id(), e);
            }
        }
    }
    @Override
    public ResourceLocation getTextureLocation(Pig pig) {
        CuteVariant variant = CuteVariants.match(pig);
        return variant != null ? variant.texture() : super.getTextureLocation(pig);
    }
    @Override
    public void render(Pig pig, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        CuteVariant variant = CuteVariants.match(pig);
        PigModel<Pig> custom = variant != null ? customModels.get(variant.id()) : null;
        this.model = custom != null ? custom : vanillaModel;
        super.render(pig, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
