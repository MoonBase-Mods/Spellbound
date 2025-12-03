package com.ombremoon.spellbound.client.model.entity.spell;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.entity.spell.Hail;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HailModel extends GeoModel<Hail> {

    @Override
    public ResourceLocation getModelResource(Hail animatable) {
        return CommonClass.customLocation("geo/entity/hail/hail.geo.json");

    }

    @Override
    public ResourceLocation getTextureResource(Hail animatable) {
        return CommonClass.customLocation("textures/entity/hail/hail.png");

    }

    @Override
    public ResourceLocation getAnimationResource(Hail animatable) {
        return CommonClass.customLocation("animations/entity/hail/hail.animation.json");

    }
}
