package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.TextListExtras;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GuideTextList(List<String> list, TextListExtras extras, ElementPosition position) implements IPageElement {
    public static final MapCodec<GuideTextList> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.listOf().fieldOf("list").forGetter(GuideTextList::list),
            TextListExtras.CODEC.optionalFieldOf("extras", TextListExtras.getDefault()).forGetter(GuideTextList::extras),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideTextList::position)
    ).apply(inst, GuideTextList::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
