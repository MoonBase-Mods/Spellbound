package com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

public record EntityRendererExtras(ResourceLocation pageScrap, boolean followMouse, int scale) implements IElementExtra {
    public static final Codec<EntityRendererExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(EntityRendererExtras::pageScrap),
            Codec.BOOL.optionalFieldOf("followMouse", false).forGetter(EntityRendererExtras::followMouse),
            Codec.INT.optionalFieldOf("scale", 25).forGetter(EntityRendererExtras::scale)
    ).apply(inst, EntityRendererExtras::new));

    public static EntityRendererExtras getDefault() {
        return new EntityRendererExtras(CommonClass.customLocation("default"), false, 25);
    }
}
