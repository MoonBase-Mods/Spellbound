package com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public record TextListExtras(ResourceLocation pageScrap, int maxRows, int rowGap, int columnGap, boolean dropShadow, int textColour, String bulletPoint) implements IElementExtra {
    public static final Codec<TextListExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(TextListExtras::pageScrap),
            Codec.INT.optionalFieldOf("maxRows", 0).forGetter(TextListExtras::maxRows),
            Codec.INT.optionalFieldOf("rowGap", 20).forGetter(TextListExtras::rowGap),
            Codec.INT.optionalFieldOf("columnGap", 45).forGetter(TextListExtras::columnGap),
            Codec.BOOL.optionalFieldOf("dropShadow", false).forGetter(TextListExtras::dropShadow),
            Codec.INT.optionalFieldOf("textColour", 0).forGetter(TextListExtras::textColour),
            Codec.STRING.optionalFieldOf("bulletPoint", "▪").forGetter(TextListExtras::bulletPoint)
    ).apply(inst, TextListExtras::new));

    public static TextListExtras getDefault() {
        return new TextListExtras(CommonClass.customLocation("default"), 0, 20, 45, false, 0, "▪");
    }

}
