package com.ombremoon.spellbound.client.renderer.types;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.model.GenericModel;
import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GenericSpellRenderer<T extends SpellEntity> extends GeoEntityRenderer<T> {
    public GenericSpellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericModel<>());
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.yRotO));
    }/*

    @Override
    public void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        VertexConsumer vertexConsumer = VertexMultiConsumer.create(bufferSource.getBuffer(SBRenderTypes.heatDistortion(getTextureLocation(animatable))), bufferSource.getBuffer(getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick)));
        super.defaultRender(poseStack, animatable, bufferSource, renderType, vertexConsumer, yaw, partialTick, packedLight);
    }*/

    @Override
    public boolean shouldRender(T spellEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
