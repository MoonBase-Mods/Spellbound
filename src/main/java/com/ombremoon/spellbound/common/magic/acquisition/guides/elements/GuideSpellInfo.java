package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.SpellInfoExtras;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GuideSpellInfo(ResourceLocation spellLoc, SpellInfoExtras extras, ElementPosition position) implements IPageElement {

    public static final MapCodec<GuideSpellInfo> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("spell").forGetter(GuideSpellInfo::spellLoc),
            SpellInfoExtras.CODEC.optionalFieldOf("extras", SpellInfoExtras.getDefault()).forGetter(GuideSpellInfo::extras),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideSpellInfo::position)
    ).apply(inst, GuideSpellInfo::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }

}
