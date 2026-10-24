package net.scottishfrogg.cutenames.client.jem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JemLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float GROUND_OFFSET = 24.0F;
    private JemLoader() {
    }
    public static JemRig load(ResourceManager resources, ResourceLocation location, JemLayout layout, ModelPart vanillaRoot) throws IOException {
        JsonObject json = read(resources, location);
        int texW = 64;
        int texH = 32;
        float[] size = floats(json.get("textureSize"), 2);
        if (size != null) {
            texW = Math.round(size[0]);
            texH = Math.round(size[1]);
        }
        JsonElement modelsElement = json.get("models");
        if (modelsElement == null || !modelsElement.isJsonArray() || modelsElement.getAsJsonArray().isEmpty()) {
            throw new IllegalStateException(location + " has no \"models\" array");
        }
        JsonArray models = modelsElement.getAsJsonArray();
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        Map<String, Pose> jemPoses = new LinkedHashMap<>();
        List<String> extraNames = new ArrayList<>();
        for (JsonElement element : models) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject m = element.getAsJsonObject();
            String part = string(m, "part");
            String id = string(m, "id");
            if (m.has("model")) {
                LOGGER.warn("[Cute Names] {}: part '{}' uses an external .jpm file, which isn't supported - skipped", location, id);
                continue;
            }
            warnUnsupported(location, m, id);
            String target = layout.resolve(part);
            if (target == null) {
                target = layout.resolve(id);
            }
            String invert = string(m, "invertAxis");
            Pose jemPose = readPose(m, invert, true);
            if (target != null && !jemPoses.containsKey(target)) {
                Pose vanillaPose = Pose.of(vanillaRoot.getChild(target));
                addPart(rootDef, target, m, invert, vanillaPose.toPartPose(), layout.wrapper(target), jemPose, true);
                jemPoses.put(target, jemPose);
                LOGGER.info("[Cute Names] {}: JEM part '{}' -> vanilla part '{}'", location, part.isEmpty() ? id : part, target);
            } else {
                String name = "cute_extra_" + extraNames.size();
                addPart(rootDef, name, m, invert, jemPose.toPartPose(), null, jemPose, true);
                extraNames.add(name);
                LOGGER.info("[Cute Names] {}: JEM part '{}' isn't a vanilla part, rendered as a static extra", location, part.isEmpty() ? id : part);
            }
        }
        Map<String, Pose> vanillaPoses = new LinkedHashMap<>();
        for (String name : layout.parts()) {
            Pose vanillaPose = Pose.of(vanillaRoot.getChild(name));
            vanillaPoses.put(name, vanillaPose);
            if (!jemPoses.containsKey(name)) {
                PartDefinition def = rootDef.addOrReplaceChild(name, CubeListBuilder.create(), vanillaPose.toPartPose());
                String inner = layout.wrapper(name);
                if (inner != null) {
                    def.addOrReplaceChild(inner, CubeListBuilder.create(), PartPose.ZERO);
                }
                jemPoses.put(name, vanillaPose);
                LOGGER.warn("[Cute Names] {}: no JEM part maps to '{}', leaving it empty", location, name);
            }
        }
        ModelPart root = LayerDefinition.create(mesh, texW, texH).bakeRoot();
        int count = layout.parts().size();
        ModelPart[] parts = new ModelPart[count];
        Pose[] jem = new Pose[count];
        Pose[] vanilla = new Pose[count];
        for (int i = 0; i < count; i++) {
            String name = layout.parts().get(i);
            parts[i] = root.getChild(name);
            jem[i] = jemPoses.get(name);
            vanilla[i] = vanillaPoses.get(name);
        }
        ModelPart[] extras = new ModelPart[extraNames.size()];
        for (int i = 0; i < extras.length; i++) {
            extras[i] = root.getChild(extraNames.get(i));
        }
        return new JemRig(root, parts, jem, vanilla, extras);
    }
    private static PartDefinition addPart(PartDefinition parent, String name, JsonObject m, String invert, PartPose pose, String wrapper, Pose ownPose, boolean topLevel) {
        CubeListBuilder cubes = cubes(m, invert, name, topLevel);
        PartDefinition outer;
        PartDefinition content;
        if (wrapper == null) {
            outer = parent.addOrReplaceChild(name, cubes, pose);
            content = outer;
        } else {
            outer = parent.addOrReplaceChild(name, CubeListBuilder.create(), pose);
            content = outer.addOrReplaceChild(wrapper, cubes, PartPose.ZERO);
        }
        int index = 0;
        for (JsonObject sub : submodels(m)) {
            if (sub.has("model")) {
                LOGGER.warn("[Cute Names] submodel '{}' uses an external .jpm file, which isn't supported - skipped", string(sub, "id"));
                continue;
            }
            String subInvert = sub.has("invertAxis") ? string(sub, "invertAxis") : invert;
            String label = string(sub, "id").isEmpty() ? string(sub, "part") : string(sub, "id");
            String subName = "sub" + (index++) + "_" + sanitize(label);
            Pose subAbs = readPose(sub, subInvert, false);
            Pose rel = new Pose(
                    subAbs.x() - ownPose.x(),
                    subAbs.y() - ownPose.y(),
                    subAbs.z() - ownPose.z(),
                    subAbs.xRot(), subAbs.yRot(), subAbs.zRot());
            addPart(content, subName, sub, subInvert, rel.toPartPose(), null, subAbs, false);
        }
        return outer;
    }
    private static CubeListBuilder cubes(JsonObject m, String invert, String partName, boolean topLevel) {
        CubeListBuilder builder = CubeListBuilder.create();
        JsonElement boxesElement = m.get("boxes");
        if (boxesElement == null || !boxesElement.isJsonArray()) {
            return builder;
        }
        boolean invertX = invert.indexOf('x') >= 0;
        boolean invertY = invert.indexOf('y') >= 0;
        boolean invertZ = invert.indexOf('z') >= 0;
        boolean mirror = string(m, "mirrorTexture").indexOf('u') >= 0;
        float[] t = topLevel ? floats(m.get("translate"), 3) : null;
        if (t == null) {
            t = new float[3];
        }
        for (JsonElement boxElement : boxesElement.getAsJsonArray()) {
            if (!boxElement.isJsonObject()) {
                continue;
            }
            JsonObject box = boxElement.getAsJsonObject();
            float[] c = floats(box.get("coordinates"), 6);
            float[] uv = floats(box.get("textureOffset"), 2);
            if (c == null) {
                continue;
            }
            if (uv == null) {
                LOGGER.warn("[Cute Names] part '{}' has a box with per-face UVs, which isn't supported - box skipped. Export with Box UV enabled.", partName);
                continue;
            }
            float grow = box.has("sizeAdd") ? box.get("sizeAdd").getAsFloat() : 0.0F;
            float x = c[0] + t[0];
            float y = c[1] + t[1];
            float z = c[2] + t[2];
            float dx = c[3];
            float dy = c[4];
            float dz = c[5];
            if (invertX) {
                x = -x - dx;
            }
            if (invertY) {
                y = -y - dy;
            }
            if (invertZ) {
                z = -z - dz;
            }
            builder.texOffs(Math.round(uv[0]), Math.round(uv[1]))
                    .mirror(mirror)
                    .addBox(x, y, z, dx, dy, dz, new CubeDeformation(grow));
        }
        return builder;
    }
    private static Pose readPose(JsonObject m, String invert, boolean topLevel) {
        float[] t = floats(m.get("translate"), 3);
        float[] r = floats(m.get("rotate"), 3);
        if (t == null) {
            t = new float[3];
        }
        if (r == null) {
            r = new float[3];
        }
        boolean invertX = invert.indexOf('x') >= 0;
        boolean invertY = invert.indexOf('y') >= 0;
        boolean invertZ = invert.indexOf('z') >= 0;
        float x;
        float y;
        float z;
        if (topLevel) {
            x = invertX ? t[0] : -t[0];
            y = invertY ? t[1] : -t[1];
            z = invertZ ? t[2] : -t[2];
        } else {
            x = invertX ? -t[0] : t[0];
            y = invertY ? -t[1] : t[1];
            z = invertZ ? -t[2] : t[2];
        }
        if (invertY) {
            y += GROUND_OFFSET;
        }
        float rx = (float) Math.toRadians(invertX ? -r[0] : r[0]);
        float ry = (float) Math.toRadians(invertY ? -r[1] : r[1]);
        float rz = (float) Math.toRadians(invertZ ? -r[2] : r[2]);
        return new Pose(x, y, z, rx, ry, rz);
    }
    private static List<JsonObject> submodels(JsonObject m) {
        List<JsonObject> out = new ArrayList<>();
        JsonElement many = m.get("submodels");
        if (many != null && many.isJsonArray()) {
            for (JsonElement e : many.getAsJsonArray()) {
                if (e.isJsonObject()) {
                    out.add(e.getAsJsonObject());
                }
            }
        }
        JsonElement one = m.get("submodel");
        if (one != null && one.isJsonObject()) {
            out.add(one.getAsJsonObject());
        }
        return out;
    }
    private static void warnUnsupported(ResourceLocation location, JsonObject m, String id) {
        if (m.has("animations")) {
            LOGGER.warn("[Cute Names] {}: part '{}' has JEM animations, which are ignored (vanilla animation drives the model)", location, id);
        }
        if (m.has("sprites")) {
            LOGGER.warn("[Cute Names] {}: part '{}' has sprites, which are ignored", location, id);
        }
        JsonElement scale = m.get("scale");
        if (scale != null && scale.isJsonPrimitive() && Math.abs(scale.getAsFloat() - 1.0F) > 1.0E-4F) {
            LOGGER.warn("[Cute Names] {}: part '{}' has a scale, which is ignored", location, id);
        }
    }
    private static JsonObject read(ResourceManager resources, ResourceLocation location) throws IOException {
        Resource resource = resources.getResource(location)
                .orElseThrow(() -> new FileNotFoundException("Missing model file " + location));
        try (InputStream in = resource.open(); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
    private static String string(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonPrimitive() ? e.getAsString() : "";
    }
    private static float[] floats(JsonElement e, int n) {
        if (e == null || !e.isJsonArray()) {
            return null;
        }
        JsonArray a = e.getAsJsonArray();
        if (a.size() < n) {
            return null;
        }
        float[] out = new float[n];
        for (int i = 0; i < n; i++) {
            out[i] = a.get(i).getAsFloat();
        }
        return out;
    }
    private static String sanitize(String s) {
        return s.replaceAll("[^A-Za-z0-9_]", "_");
    }
}
