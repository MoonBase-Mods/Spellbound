package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.renderer.types.EmissiveOutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.ShatteringCrystal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ShatteringCrystalRenderer extends EmissiveOutlineSpellRenderer<ShatteringCrystal> {
    public ShatteringCrystalRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(ShatteringCrystal animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.translate(0.0F, 0.75F, 0.0F);
        float f3 = animatable.getSpin(partialTick);
        poseStack.mulPose(Axis.YP.rotation(f3));
    }
}
