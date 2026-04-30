package com.ombremoon.spellbound.client.model.types;

import com.ombremoon.spellbound.common.world.entity.IVFXEntity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VFXSpellModel<T extends IVFXEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return CommonClass.customLocation("geo/entity/mushroom_projectile/mushroom_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return CommonClass.customLocation("textures/entity/mushroom_projectile/mushroom_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return CommonClass.customLocation("animations/entity/mushroom_projectile/mushroom_projectile.animation.json");
    }
}
