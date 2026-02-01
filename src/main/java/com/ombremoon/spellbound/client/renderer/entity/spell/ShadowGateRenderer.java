package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.renderer.types.OutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.ShadowGate;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ShadowGateRenderer extends OutlineSpellRenderer<ShadowGate> {
    public ShadowGateRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(ShadowGate animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        if (animatable.getSummoner() != null) {
            if (animatable.isShifted()) {
                poseStack.translate(0, -0.05F, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(30));
            }
        }
    }
}
