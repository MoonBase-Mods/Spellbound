package com.ombremoon.spellbound.client.model.entity.spell;

import com.ombremoon.spellbound.common.world.entity.spell.IceShrapnel;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceShrapnelModel extends GeoModel<IceShrapnel> {

    @Override
    public ResourceLocation getModelResource(IceShrapnel animatable) {
        return CommonClass.customLocation("geo/entity/ice_shrapnel/ice_shrapnel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceShrapnel animatable) {
        return CommonClass.customLocation("textures/entity/ice_shrapnel/ice_shrapnel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceShrapnel animatable) {
        return CommonClass.customLocation("animations/entity/ice_shrapnel/ice_shrapnel.animation.json");
    }
}
