package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.SpellInfoExtras;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
