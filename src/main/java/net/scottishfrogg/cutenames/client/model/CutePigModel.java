package net.scottishfrogg.cutenames.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import net.scottishfrogg.cutenames.CuteMod;
import net.scottishfrogg.cutenames.client.jem.JemRig;

public class CutePigModel extends PigModel<Pig> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(CuteMod.MOD_ID, "cute_pig"), "main");
    private final JemRig rig;
    public CutePigModel(ModelPart root) {
        this(root, null);
    }
    public CutePigModel(JemRig rig) {
        this(rig.root(), rig);
    }
    private CutePigModel(ModelPart root, JemRig rig) {
        super(root);
        this.rig = rig;
    }
    @Override
    public void prepareMobModel(Pig entity, float limbSwing, float limbSwingAmount, float partialTick) {
        if (rig != null) rig.reset();
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
    @Override
    public void setupAnim(Pig entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F)
                .texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F),
                PartPose.offset(0.0F, 12.0F, -6.0F));
        head.addOrReplaceChild("crown", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-4.0F, -7.0F, -8.0F, 8.0F, 3.0F, 8.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(0, 26).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-3.0F, 18.0F, 7.0F));
        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(0, 26).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(3.0F, 18.0F, 7.0F));
        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-3.0F, 18.0F, -5.0F));
        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(3.0F, 18.0F, -5.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
