package net.scottishfrogg.cutenames.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class GatedLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final RenderLayer<T, M> delegate;
    private final Predicate<T> shouldRender;
    public GatedLayer(RenderLayerParent<T, M> parent, RenderLayer<T, M> delegate, Predicate<T> shouldRender) {
        super(parent);
        this.delegate = delegate;
        this.shouldRender = shouldRender;
    }
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (shouldRender.test(entity)) {
            delegate.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
