package com.ombremoon.spellbound.client.renderer.entity;

import com.ombremoon.spellbound.common.world.entity.living.Hellhound;
import com.ombremoon.spellbound.common.world.entity.living.wolf.SummonedWolfRenderer;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class HellhoundRenderer extends SummonedWolfRenderer<Hellhound> {
    public HellhoundRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Hellhound entity) {
        return CommonClass.customLocation("textures/entity/hellhound/hellhound.png");
    }
}
