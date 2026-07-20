package com.ombremoon.spellbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.common.world.SpellDimensionData;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class SpellDimensionDebugRenderer {
    private static boolean enabled = false;
    @Nullable
    private static AABB structureBounds = null;
    @Nullable
    private static BlockPos spawnPos = null;
    @Nullable
    private static BlockPos originPos = null;

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static void setStructureBounds(@Nullable AABB bounds) {
        structureBounds = bounds;
    }

    public static void setSpawnPos(@Nullable BlockPos pos) {
        spawnPos = pos;
    }

    public static void setOriginPos(@Nullable BlockPos pos) {
        originPos = pos;
    }

    public static void clear() {
        structureBounds = null;
        spawnPos = null;
        originPos = null;
    }

    public static void render(PoseStack poseStack, Camera camera, MultiBufferSource.BufferSource bufferSource) {
        if (!enabled) return;

        if (!SpellDimensionData.isStatic(camera.getEntity().level())) return;

        Vec3 camPos = camera.getPosition();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());

        if (structureBounds != null) {
            LevelRenderer.renderLineBox(poseStack, lineConsumer, structureBounds, 1.0f, 0.0f, 0.0f, 1.0f);
            AABB centerBox = new AABB(BlockPos.containing(structureBounds.getCenter())).inflate(0.25);
            LevelRenderer.renderLineBox(poseStack, lineConsumer, centerBox, 1.0f, 0.0f, 1.0f, 1.0f);
        }

        if (spawnPos != null) {
            AABB spawnBox = new AABB(spawnPos).inflate(0.5);
            LevelRenderer.renderLineBox(poseStack, lineConsumer, spawnBox, 0.0f, 1.0f, 0.0f, 1.0f);
        }

        if (originPos != null) {
            AABB originBox = new AABB(originPos).inflate(0.25);
            LevelRenderer.renderLineBox(poseStack, lineConsumer, originBox, 0.0f, 0.0f, 1.0f, 1.0f);
        }

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }
}
