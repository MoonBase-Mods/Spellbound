package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.common.world.entity.spell.SolarRay;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import software.bernie.geckolib.util.Color;

public class SolarRayRendererTest extends EntityRenderer<SolarRay> {
    public static final ResourceLocation SOLAR_RAY_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    public SolarRayRendererTest(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SolarRay solarRay, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Entity owner = solarRay.getOwner();
        if (owner == null) return;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-owner.getYRot()));
        poseStack.translate(0.0F, 1.25F, 1.25F);
//        renderBeamLayer(poseStack, bufferSource, partialTick, solarRay.level().getGameTime(), 12.0F, FastColor.ARGB32.color(64, Color.WHITE.argbInt()), 0.5F, 0.25F);
        renderBeamLayer(poseStack, bufferSource, partialTick, solarRay.level().getGameTime(), 12.0F, Color.WHITE.argbInt(), 0.4F, 0.125F);
        poseStack.popPose();
        super.render(solarRay, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderBeamLayer(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks, long gameTime, float beamLength, int color, float radius, float textureScale) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.beaconBeam(SOLAR_RAY_LOCATION, true));
        Matrix4f matrix = poseStack.last().pose();
        float vStart = -gameTime * 0.01F % 1.0F;
        float vEnd = beamLength * textureScale + vStart;
        poseStack.mulPose(Axis.ZP.rotationDegrees(6.25F * gameTime + partialTicks));

        renderQuad(consumer, matrix, color, -radius, radius, radius, radius, 0, beamLength, vStart, vEnd); // Top face
        renderQuad(consumer, matrix, color, radius, -radius, radius, radius, 0, beamLength, vStart, vEnd); // Top face

        renderQuad(consumer, matrix, color, radius, radius, radius, -radius, 0, beamLength, vStart, vEnd); // Right face
        renderQuad(consumer, matrix, color, radius, radius, -radius, radius, 0, beamLength, vStart, vEnd); // Right face

        renderQuad(consumer, matrix, color, -radius, -radius, -radius, radius, 0, beamLength, vStart, vEnd); // Left face
        renderQuad(consumer, matrix, color, -radius, -radius, radius, -radius, 0, beamLength, vStart, vEnd); // Left face

        renderQuad(consumer, matrix, color, radius, -radius, -radius, -radius, 0, beamLength, vStart, vEnd); // Bottom face
        renderQuad(consumer, matrix, color, -radius, radius, -radius, -radius, 0, beamLength, vStart, vEnd); // Bottom face
    }

    private static void renderQuad(VertexConsumer consumer, Matrix4f matrix4f, int color, float x1, float x2, float y1, float y2, float zStart, float zEnd, float vStart, float vEnd) {
        addVertex(consumer, matrix4f, color, x1, y1, zStart, 1.0F, vStart);
        addVertex(consumer, matrix4f, color, x1, y1, zEnd, 1.0F, vEnd);
        addVertex(consumer, matrix4f, color, x2, y2, zEnd, 0.0F, vEnd);
        addVertex(consumer, matrix4f, color, x2, y2, 0, 0.0F, vStart);
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix4f, int color, float x, float y, float z, float u, float v) {
        consumer.addVertex(matrix4f, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(SolarRay entity) {
        return SOLAR_RAY_LOCATION;
    }
}
