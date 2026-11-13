package com.ombremoon.spellbound.client.renderer.layer;

import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpellLayerModel<T extends AbstractSpell> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        ResourceLocation location = animatable.spellLayer();
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "geo/spell_layer/" + location.getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        ResourceLocation location = animatable.spellLayer();
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/spell_layer/" + location.getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        ResourceLocation location = animatable.spellLayer();
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "animations/spell_layer/" + location.getPath() + ".animation.json");
    }
}
