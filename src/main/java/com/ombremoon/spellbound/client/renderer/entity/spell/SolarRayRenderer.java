package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.renderer.types.EmissiveOutlineSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.SolarRay;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SolarRayRenderer extends EmissiveOutlineSpellRenderer<SolarRay> {

    public SolarRayRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void applyRotations(SolarRay animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
//        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        Entity owner = animatable.getSummoner();
        if (owner == null) return;
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, owner.yRotO, owner.getYRot())));
    }

    @Override
    public boolean shouldRender(SolarRay spellEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
