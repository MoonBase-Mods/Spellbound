package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ItemExtras;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GuideItem(ResourceLocation itemLoc, String tileName, float scale, ElementPosition position, ItemExtras extras) implements IPageElement {
    public static final MapCodec<GuideItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(GuideItem::itemLoc),
            Codec.STRING.optionalFieldOf("tileName", "basic").forGetter(GuideItem::tileName),
            Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(GuideItem::scale),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideItem::position),
            ItemExtras.CODEC.optionalFieldOf("extras", ItemExtras.getDefault()).forGetter(GuideItem::extras)
    ).apply(inst, GuideItem::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
