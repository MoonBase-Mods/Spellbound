package com.ombremoon.spellbound.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.model.entity.projectile.VFXProjectileModel;
import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VFXProjectileRenderer extends GeoEntityRenderer<SpellProjectile<?>> {
    public VFXProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VFXProjectileModel());
    }

    @Override
    public void render(SpellProjectile<?> entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

    }

    @Override
    protected void applyRotations(SpellProjectile<?> animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) + 90.0F));
    }
}
