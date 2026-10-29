package com.ombremoon.spellbound.client.renderer.entity;

import com.ombremoon.spellbound.common.world.entity.living.SpectralWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SpectralWolfRenderer extends SummonedWolfRenderer<SpectralWolf> {
    public SpectralWolfRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SpectralWolf entity) {
        return ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_snowy.png");
    }
}
