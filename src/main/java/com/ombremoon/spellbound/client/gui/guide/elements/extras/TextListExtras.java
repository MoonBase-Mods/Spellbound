package com.ombremoon.spellbound.client.gui.guide.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

public record TextListExtras(int maxRows, int rowGap, int columnGap, int lineLength, boolean dropShadow, int textColour, String bulletPoint) implements IElementExtra {
    public static final Codec<TextListExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("maxRows", 0).forGetter(TextListExtras::maxRows),
            Codec.INT.optionalFieldOf("rowGap", 20).forGetter(TextListExtras::rowGap),
            Codec.INT.optionalFieldOf("columnGap", 45).forGetter(TextListExtras::columnGap),
            Codec.INT.optionalFieldOf("lineLength", 150).forGetter(TextListExtras::lineLength),
            Codec.BOOL.optionalFieldOf("dropShadow", false).forGetter(TextListExtras::dropShadow),
            Codec.INT.optionalFieldOf("textColour", 0).forGetter(TextListExtras::textColour),
            Codec.STRING.optionalFieldOf("bulletPoint", "▪").forGetter(TextListExtras::bulletPoint)
    ).apply(inst, TextListExtras::new));

    public static TextListExtras getDefault() {
        return new TextListExtras(0, 20, 45, 150, false, 0, "▪");
    }

}
