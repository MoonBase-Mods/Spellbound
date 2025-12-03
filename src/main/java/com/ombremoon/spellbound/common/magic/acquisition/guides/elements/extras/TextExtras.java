package com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record TextExtras(ResourceLocation pageScrap, int colour, int maxLineLength, int lineGap, boolean dropShadow, boolean textWrapping) implements IElementExtra {
    public static final Codec<TextExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(TextExtras::pageScrap),
            Codec.INT.optionalFieldOf("colour", 0).forGetter(TextExtras::colour),
            Codec.INT.optionalFieldOf("maxLineLength", 150).forGetter(TextExtras::maxLineLength),
            Codec.INT.optionalFieldOf("lineGap", 9).forGetter(TextExtras::lineGap),
            Codec.BOOL.optionalFieldOf("dropShadow", false).forGetter(TextExtras::dropShadow),
            Codec.BOOL.optionalFieldOf("textWrapping", true).forGetter(TextExtras::textWrapping)
    ).apply(inst, TextExtras::new));

    public static TextExtras getDefault() {
        return new TextExtras(CommonClass.customLocation("default"), 0, 150, 9, false, true);
    }


}
