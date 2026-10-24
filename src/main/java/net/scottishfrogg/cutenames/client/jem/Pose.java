package net.scottishfrogg.cutenames.client.jem;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

record Pose(float x, float y, float z, float xRot, float yRot, float zRot) {
    static Pose of(ModelPart part) {
        return new Pose(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot);
    }
    PartPose toPartPose() {
        return PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot);
    }
    void load(ModelPart part) {
        part.x = x;
        part.y = y;
        part.z = z;
        part.xRot = xRot;
        part.yRot = yRot;
        part.zRot = zRot;
    }
}
