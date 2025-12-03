package com.ombremoon.spellbound.client.model.entity.projectile;

import com.ombremoon.spellbound.common.world.entity.projectile.MushroomProjectile;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MushroomProjectileModel extends GeoModel<MushroomProjectile> {
    @Override
    public ResourceLocation getModelResource(MushroomProjectile animatable) {
        return CommonClass.customLocation("geo/entity/mushroom_projectile/mushroom_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MushroomProjectile animatable) {
        return CommonClass.customLocation("textures/entity/mushroom_projectile/mushroom_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MushroomProjectile animatable) {
        return CommonClass.customLocation("animations/entity/mushroom_projectile/mushroom_projectile.animation.json");
    }
}
