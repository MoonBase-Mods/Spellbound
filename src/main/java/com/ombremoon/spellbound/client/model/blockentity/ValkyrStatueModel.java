package com.ombremoon.spellbound.client.model.blockentity;

import com.ombremoon.spellbound.common.world.block.entity.ValkyrBlockEntity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ValkyrStatueModel extends GeoModel<ValkyrBlockEntity> {
    @Override
    public ResourceLocation getModelResource(ValkyrBlockEntity animatable) {
        return CommonClass.customLocation("geo/block/valkyr_statue.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ValkyrBlockEntity animatable) {
        return CommonClass.customLocation("textures/block/valkyr_statue.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ValkyrBlockEntity animatable) {
        return CommonClass.customLocation("animations/block/valkyr_statue.animation.json");
    }
}
