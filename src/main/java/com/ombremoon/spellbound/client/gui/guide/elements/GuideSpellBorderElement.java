package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record GuideSpellBorderElement(SpellPath path, Optional<SpellMastery> mastery, int colour, ElementPosition position, ResourceLocation pathTexture, Optional<Component> topText, Optional<Component> bottomText) implements IPageElement {
    public static final MapCodec<GuideSpellBorderElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SpellPath.CODEC.fieldOf("path").forGetter(GuideSpellBorderElement::path),
            SpellMastery.CODEC.optionalFieldOf("mastery").forGetter(GuideSpellBorderElement::mastery),
            Codec.INT.fieldOf("colour").forGetter(GuideSpellBorderElement::colour),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideSpellBorderElement::position),
            ResourceLocation.CODEC.fieldOf("pathTexture").forGetter(GuideSpellBorderElement::pathTexture),
            ComponentSerialization.CODEC.optionalFieldOf("topText").forGetter(GuideSpellBorderElement::topText),
            ComponentSerialization.CODEC.optionalFieldOf("bottomText").forGetter(GuideSpellBorderElement::bottomText)
    ).apply(inst, GuideSpellBorderElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
