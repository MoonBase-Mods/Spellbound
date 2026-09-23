package com.ombremoon.spellbound.common.world.entity.living.wolf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Wolf;

public abstract class SummonedWolfRenderer<T extends Wolf> extends MobRenderer<T, WolfModel<T>> {
    public SummonedWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new WolfModel<>(context.bakeLayer(ModelLayers.WOLF)), 0.5F);
    }

    @Override
    protected float getBob(T livingBase, float partialTick) {
        return livingBase.getTailAngle();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isWet()) {
            float f = entity.getWetShade(partialTicks);
            this.model.setColor(FastColor.ARGB32.colorFromFloat(1.0F, f, f, f));
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        if (entity.isWet()) {
            this.model.setColor(-1);
        }
    }
}
