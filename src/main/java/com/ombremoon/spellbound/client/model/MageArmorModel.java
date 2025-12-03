package com.ombremoon.spellbound.client.model;

import com.ombremoon.spellbound.common.world.item.MageArmorItem;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MageArmorModel extends GeoModel<MageArmorItem> {
    @Override
    public ResourceLocation getModelResource(MageArmorItem animatable) {
        return CommonClass.customLocation("geo/armor/" + getName(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MageArmorItem animatable) {
        return CommonClass.customLocation("textures/armor/" + getName(animatable) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(MageArmorItem animatable) {
        return CommonClass.customLocation("animations/armor/" + getName(animatable) + ".animation.json");
    }

    protected String getName(MageArmorItem animatable) {
        return BuiltInRegistries.ARMOR_MATERIAL.getKey(animatable.getMaterial().value()).getPath();
    }
}
