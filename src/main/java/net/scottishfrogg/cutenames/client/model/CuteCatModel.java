package net.scottishfrogg.cutenames.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;
import net.scottishfrogg.cutenames.CuteMod;
import net.scottishfrogg.cutenames.client.jem.JemRig;

public class CuteCatModel extends CatModel<Cat> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(CuteMod.MOD_ID, "cute_cat"), "main");
    private final JemRig rig;
    public CuteCatModel(ModelPart root) {
        this(root, null);
    }
    public CuteCatModel(JemRig rig) {
        this(rig.root(), rig);
    }
    private CuteCatModel(ModelPart root, JemRig rig) {
        super(root);
        this.rig = rig;
    }
    @Override
    public void prepareMobModel(Cat entity, float limbSwing, float limbSwingAmount, float partialTick) {
        if (rig != null) rig.reset();
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
    @Override
    public void setupAnim(Cat entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (rig != null) rig.apply();
    }
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        super.renderToBuffer(poseStack, buffer, light, overlay, r, g, b, a);
        if (rig != null) rig.renderExtras(poseStack, buffer, light, overlay, r, g, b, a);
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F)
                .texOffs(0, 24).addBox(-1.5F, -0.02F, -4.0F, 3.0F, 2.0F, 2.0F)
                .texOffs(6, 10).addBox(1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F)
                .texOffs(0, 10).addBox(-2.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F),
                PartPose.offset(0.0F, 15.0F, -9.0F));
        head.addOrReplaceChild("whisker", CubeListBuilder.create()
                .texOffs(16, 23).mirror().addBox(-3.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F),
                PartPose.offsetAndRotation(-1.5F, 0.48F, -4.0F, 0.0F, (float) Math.toRadians(-22.5), 0.0F));
        head.addOrReplaceChild("whisker2", CubeListBuilder.create()
                .texOffs(16, 23).addBox(0.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F),
                PartPose.offsetAndRotation(1.5F, 0.48F, -4.0F, 0.0F, (float) Math.toRadians(22.5), 0.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(20, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 12.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(1.1F, 14.1F, -5.0F));
        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(-1.1F, 14.1F, -5.0F));
        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(8, 21).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(1.1F, 18.0F, 5.0F));
        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(8, 13).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-1.1F, 18.0F, 5.0F));
        root.addOrReplaceChild("tail1", CubeListBuilder.create()
                .texOffs(0, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 15.5F, 8.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        root.addOrReplaceChild("tail2", CubeListBuilder.create()
                .texOffs(4, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 15.5F, 16.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }
}
