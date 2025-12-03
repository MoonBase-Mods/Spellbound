package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.sentinellib.api.Easing;
import com.ombremoon.spellbound.client.renderer.types.OutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.StormCloud;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class StormCloudRenderer extends OutlineSpellRenderer<StormCloud> {
    public StormCloudRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(StormCloud animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        float easing = Math.min(2, Easing.QUAD_IN.easing(2, (float) animatable.tickCount / 100));
        poseStack.scale(easing, easing, easing);
        poseStack.translate(0, -4.5, 0.25);
    }
}
