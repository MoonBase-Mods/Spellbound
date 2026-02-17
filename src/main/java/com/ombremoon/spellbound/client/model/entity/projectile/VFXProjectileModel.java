package com.ombremoon.spellbound.client.model.entity.projectile;

import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VFXProjectileModel extends GeoModel<SpellProjectile<?>> {
    @Override
    public ResourceLocation getModelResource(SpellProjectile<?> animatable) {
        return CommonClass.customLocation("geo/entity/mushroom_projectile/mushroom_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpellProjectile<?> animatable) {
        return CommonClass.customLocation("textures/entity/mushroom_projectile/mushroom_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellProjectile<?> animatable) {
        return CommonClass.customLocation("animations/entity/mushroom_projectile/mushroom_projectile.animation.json");
    }
}
