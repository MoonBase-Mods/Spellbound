package com.ombremoon.spellbound.client.model.entity.spell;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpellProjectileModel<T extends SpellProjectile> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return CommonClass.customLocation("geo/entity/" + getName(animatable) + "/" + getName(animatable) + ".geo.json");

    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return CommonClass.customLocation("textures/entity/" + getName(animatable) + "/" + getName(animatable) + ".png");

    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return CommonClass.customLocation("animations/entity/" + getName(animatable) + "/" + getName(animatable) + ".animation.json");

    }

    protected String getName(T spell) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(spell.getType()).getPath();
    }
}
