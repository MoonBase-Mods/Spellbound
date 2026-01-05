package com.ombremoon.spellbound.client.gui.guide.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

public record EntityRendererExtras(ResourceLocation pageScrap, boolean followMouse, float scale, float xRot, float yRot, float zRot, boolean animated) implements IElementExtra {
    public static final Codec<EntityRendererExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(EntityRendererExtras::pageScrap),
            Codec.BOOL.optionalFieldOf("followMouse", false).forGetter(EntityRendererExtras::followMouse),
            Codec.FLOAT.optionalFieldOf("scale", 25F).forGetter(EntityRendererExtras::scale),
            Codec.FLOAT.optionalFieldOf("xRot", 0F).forGetter(EntityRendererExtras::xRot),
            Codec.FLOAT.optionalFieldOf("yRot", 0F).forGetter(EntityRendererExtras::yRot),
            Codec.FLOAT.optionalFieldOf("zRot", 0F).forGetter(EntityRendererExtras::zRot),
            Codec.BOOL.optionalFieldOf("animated", false).forGetter(EntityRendererExtras::animated)
    ).apply(inst, EntityRendererExtras::new));

    public static EntityRendererExtras getDefault() {
        return new EntityRendererExtras(CommonClass.customLocation("default"), false, 25, 0, 0,0, false);
    }
}
