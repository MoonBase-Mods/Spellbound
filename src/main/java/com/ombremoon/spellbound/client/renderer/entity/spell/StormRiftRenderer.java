package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.sentinellib.api.Easing;
import com.ombremoon.spellbound.client.renderer.types.EmissiveOutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.StormRift;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class StormRiftRenderer extends EmissiveOutlineSpellRenderer<StormRift> {

    public StormRiftRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(StormRift animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        int maxGrowth = animatable.canGrow() ? 2 : 1;
        float easing = Math.min(maxGrowth, Easing.QUAD_IN.easing(2, (float) animatable.tickCount / 100));
        poseStack.scale(easing, easing, easing);

        if (animatable.canRotate()) {
            float f = Easing.QUAD_IN.easing(3, (float) animatable.rotationTick / 200);
            animatable.rotationAngle += Math.min(3, f);
            poseStack.mulPose(Axis.YP.rotationDegrees(animatable.rotationAngle));
        }
    }
}
