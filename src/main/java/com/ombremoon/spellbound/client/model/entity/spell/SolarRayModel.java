package com.ombremoon.spellbound.client.model.entity.spell;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.entity.spell.SolarRay;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SolarRayModel extends GeoModel<SolarRay> {

    @Override
    public ResourceLocation getModelResource(SolarRay animatable) {
        return CommonClass.customLocation("geo/entity/solar_ray/" + getName(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SolarRay animatable) {
        return CommonClass.customLocation("textures/entity/solar_ray/solar_ray.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SolarRay animatable) {
        return CommonClass.customLocation("animations/entity/solar_ray/" + getName(animatable) + ".animation.json");
    }

    protected String getName(SolarRay animatable) {
        String name = "solar_ray";
        if (animatable.hasSunshine())
            name = name + "_extended";

        return name;
    }
}
