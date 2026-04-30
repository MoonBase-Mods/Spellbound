package com.ombremoon.spellbound.client.photon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public final class HeldItemTransformCapture {
    private static final Vector3f LOCAL_OFFSET = new Vector3f(0.0F, 0.05F, -0.4F);
    private static final Map<Long, Captured> CAPTURED = new HashMap<>();
    private static long currentFrame = 0L;

    private HeldItemTransformCapture() {}

    public record Captured(Vec3 worldPos, Quaternionf rotation, long frame) {}

    public static void onFrameStart() {
        currentFrame++;
        CAPTURED.entrySet().removeIf(e -> currentFrame - e.getValue().frame() > 1);
    }

    public static void capture(int entityId, InteractionHand hand, PoseStack poseStack) {
        Matrix4f m = new Matrix4f(poseStack.last().pose());
        Vector4f local = new Vector4f(LOCAL_OFFSET.x, LOCAL_OFFSET.y, LOCAL_OFFSET.z, 1.0F);
        m.transform(local);
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 worldPos = new Vec3(local.x + cameraPos.x, local.y + cameraPos.y, local.z + cameraPos.z);
        Quaternionf rot = m.getNormalizedRotation(new Quaternionf());
        CAPTURED.put(key(entityId, hand), new Captured(worldPos, rot, currentFrame));
    }

    public static Captured get(int entityId, InteractionHand hand) {
        Captured c = CAPTURED.get(key(entityId, hand));
        if (c == null) return null;
        if (currentFrame - c.frame() > 1) return null;
        return c;
    }

    private static long key(int entityId, InteractionHand hand) {
        return ((long) entityId << 1) | hand.ordinal();
    }
}
