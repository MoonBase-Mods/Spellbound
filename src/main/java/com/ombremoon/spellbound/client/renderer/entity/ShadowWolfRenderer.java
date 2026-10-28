package com.ombremoon.spellbound.client.renderer.entity;

import com.ombremoon.spellbound.common.world.entity.living.Hellhound;
import com.ombremoon.spellbound.common.world.entity.living.ShadowWolf;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ShadowWolfRenderer extends SummonedWolfRenderer<ShadowWolf> {
    public ShadowWolfRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ShadowWolf entity) {
        return CommonClass.customLocation("textures/entity/shadow_wolf/shadow_wolf.png");
    }
}
