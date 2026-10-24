package net.scottishfrogg.cutenames.client.jem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public final class JemRig {
    private final ModelPart root;
    private final ModelPart[] parts;
    private final Pose[] jemPoses;
    private final Pose[] vanillaPoses;
    private final ModelPart[] extras;
    JemRig(ModelPart root, ModelPart[] parts, Pose[] jemPoses, Pose[] vanillaPoses, ModelPart[] extras) {
        this.root = root;
        this.parts = parts;
        this.jemPoses = jemPoses;
        this.vanillaPoses = vanillaPoses;
        this.extras = extras;
    }
    public ModelPart root() {
        return root;
    }
    public void reset() {
        for (int i = 0; i < parts.length; i++) {
            vanillaPoses[i].load(parts[i]);
        }
    }
    public void apply() {
        for (int i = 0; i < parts.length; i++) {
            ModelPart p = parts[i];
            Pose j = jemPoses[i];
            Pose v = vanillaPoses[i];
            p.x = j.x() + (p.x - v.x());
            p.y = j.y() + (p.y - v.y());
            p.z = j.z() + (p.z - v.z());
            p.xRot = j.xRot() + (p.xRot - v.xRot());
            p.yRot = j.yRot() + (p.yRot - v.yRot());
            p.zRot = j.zRot() + (p.zRot - v.zRot());
        }
    }
    public void renderExtras(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        for (ModelPart extra : extras) {
            extra.render(poseStack, buffer, light, overlay, r, g, b, a);
        }
    }
}
