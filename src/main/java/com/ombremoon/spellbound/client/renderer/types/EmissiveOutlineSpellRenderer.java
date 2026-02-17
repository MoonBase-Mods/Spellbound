package com.ombremoon.spellbound.client.renderer.types;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EmissiveOutlineSpellRenderer<T extends SpellEntity<?>> extends OutlineSpellRenderer<T> {
    public EmissiveOutlineSpellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
