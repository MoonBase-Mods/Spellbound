package com.ombremoon.spellbound.client.renderer.types;

import com.ombremoon.spellbound.client.model.LivingModel;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GenericLivingEntityRenderer<T extends SBLivingEntity> extends GeoEntityRenderer<T> {

    public GenericLivingEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LivingModel<>());
    }

    public GenericLivingEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0.0F;
    }

    @Override
    public int getPackedOverlay(T animatable, float u, float partialTick) {
        return animatable.isDeadOrDying() ? OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(false)) : super.getPackedOverlay(animatable, u, partialTick);
    }
}
