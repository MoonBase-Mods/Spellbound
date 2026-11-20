package com.ombremoon.spellbound.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.common.world.entity.projectile.BoundArrow;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class BoundArrowRenderer extends EntityRenderer<BoundArrow> {
    public BoundArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BoundArrow entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        var variant = entity.getVariant();
        float f = variant.getUvOffset();
        float f1 = f + 0.15625F;

        float f2 = (float)entity.shakeTime - partialTicks;
        if (f2 > 0.0F) {
            float f3 = -Mth.sin(f2 * 3.0F) * f2;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f3));
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-4.0F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        PoseStack.Pose posestack$pose = poseStack.last();
        if (variant == BoundArrow.ArrowVariant.MAGIC) {
            this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, f, -1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, f, f, -1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, f, f1, -1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, f1, -1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, f, 1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, f, f, 1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, f, f1, 1, 0, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, f1, 1, 0, 0, packedLight);
        }

        for (int j = 0; j < 4; j++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            this.vertex(posestack$pose, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, 8, 2, 0, 0.5F, f, 0, 1, 0, packedLight);
            this.vertex(posestack$pose, vertexconsumer, -8, 2, 0, 0.0F, f, 0, 1, 0, packedLight);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public void vertex(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int x,
            int y,
            int z,
            float u,
            float v,
            int normalX,
            int normalY,
            int normalZ,
            int packedLight
    ) {
        consumer.addVertex(pose, (float)x, (float)y, (float)z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, (float)normalX, (float)normalZ, (float)normalY);
    }

    @Override
    public ResourceLocation getTextureLocation(BoundArrow entity) {
        var variant = entity.getVariant();
        String name = variant.getName();
        return CommonClass.customLocation("textures/entity/bound_arrow/" + name + "_arrow.png");
    }

}
