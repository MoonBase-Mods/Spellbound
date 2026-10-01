package com.ombremoon.spellbound.client.renderer.entity.projectile;

import com.ombremoon.spellbound.common.world.entity.projectile.BoundArrow;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BoundArrowRenderer extends ArrowRenderer<BoundArrow> {
    public BoundArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BoundArrow entity) {
        var variant = entity.getVariant();
        String name = variant.getName();
        return CommonClass.customLocation("textures/entity/bound_arrow/" + name + "_arrow.png");
    }

}
