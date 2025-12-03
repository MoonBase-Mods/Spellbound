/*
package com.ombremoon.spellbound.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.renderer.types.OutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.Cyclone;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CycloneRenderer extends OutlineSpellRenderer<Cyclone> {
    public CycloneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(Cyclone animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        float scale = 0.125F * (animatable.getStacks());
        poseStack.scale(scale, scale, scale);
    }
}
*/
