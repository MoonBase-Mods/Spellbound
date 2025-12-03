package com.ombremoon.spellbound.client.model;

import com.ombremoon.spellbound.common.world.item.CatalystItem;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SBCatalystModel extends GeoModel<CatalystItem> {
    @Override
    public ResourceLocation getModelResource(CatalystItem animatable) {
        return CommonClass.customLocation("geo/item/staff/" + getName(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CatalystItem animatable) {
        return CommonClass.customLocation("textures/item/staff/" + getName(animatable) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CatalystItem animatable) {
        return CommonClass.customLocation("animations/item/staff/" + getName(animatable) + ".animation.json");
    }

    protected String getName(CatalystItem animatable) {
        return BuiltInRegistries.ITEM.getKey(animatable).getPath();
    }
}
