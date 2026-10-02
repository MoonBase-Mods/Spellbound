package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IClickable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GuideMultiBlockElement(ResourceLocation structure, ResourceLocation multiblock, int scale, int detailedScale, ElementPosition position) implements IPageElement, IClickable, IHoverable {

    public static final MapCodec<GuideMultiBlockElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("structure", CommonClass.customLocation("default")).forGetter(GuideMultiBlockElement::structure),
            ResourceLocation.CODEC.optionalFieldOf("multiblock", CommonClass.customLocation("default")).forGetter(GuideMultiBlockElement::multiblock),
            Codec.INT.optionalFieldOf("scale", 2).forGetter(GuideMultiBlockElement::scale),
            Codec.INT.optionalFieldOf("detailed_scale", 20).forGetter(GuideMultiBlockElement::detailedScale),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideMultiBlockElement::position)
    ).apply(inst, GuideMultiBlockElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }

    @Override
    public RenderOrder getRenderOrder() {
        return RenderOrder.ABOVE;
    }
}
