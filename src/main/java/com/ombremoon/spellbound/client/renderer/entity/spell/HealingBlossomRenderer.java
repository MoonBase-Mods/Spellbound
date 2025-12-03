package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.model.GenericModel;
import com.ombremoon.spellbound.common.world.entity.spell.HealingBlossom;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HealingBlossomRenderer extends GeoEntityRenderer<HealingBlossom> {
    public HealingBlossomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericModel<>());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preRender(PoseStack poseStack, HealingBlossom animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.translate(0, 0.1f+(Math.sin(animatable.tickCount/50f)*0.08f), 0);
    }


    @Override
    protected void applyRotations(HealingBlossom animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.mulPose(Axis.YP.rotationDegrees(animatable.tickCount % 360));
    }

    @Override
    public boolean shouldRender(HealingBlossom livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
