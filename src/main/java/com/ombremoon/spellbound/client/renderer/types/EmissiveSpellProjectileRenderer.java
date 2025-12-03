package com.ombremoon.spellbound.client.renderer.types;

import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EmissiveSpellProjectileRenderer<T extends SpellProjectile> extends SpellProjectileRenderer<T> {
    public EmissiveSpellProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    public EmissiveSpellProjectileRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
