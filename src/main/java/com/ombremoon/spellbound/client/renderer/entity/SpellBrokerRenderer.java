package com.ombremoon.spellbound.client.renderer.entity;

import com.ombremoon.spellbound.client.model.entity.spell.SpellBrokerModel;
import com.ombremoon.spellbound.common.world.entity.living.SpellBroker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpellBrokerRenderer extends GeoEntityRenderer<SpellBroker> {
    public SpellBrokerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpellBrokerModel());
    }
}
