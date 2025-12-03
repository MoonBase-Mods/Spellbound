package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.renderer.types.EmissiveOutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.StormBolt;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class StormBoltRenderer extends EmissiveOutlineSpellRenderer<StormBolt> {
    public StormBoltRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(StormBolt animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.scale(2, 2, 2);
    }
}
