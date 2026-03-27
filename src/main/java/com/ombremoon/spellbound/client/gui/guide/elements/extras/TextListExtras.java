package com.ombremoon.spellbound.client.gui.guide.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

public record TextListExtras(int maxRows, int rowGap, int columnGap, int lineLength, boolean dropShadow, int textColour, String bulletPoint, boolean underlineClickable, boolean centered) implements IElementExtra {
    public static final Codec<TextListExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("maxColumns", 0).forGetter(TextListExtras::maxRows),
            Codec.INT.optionalFieldOf("rowGap", 20).forGetter(TextListExtras::rowGap),
            Codec.INT.optionalFieldOf("columnGap", 45).forGetter(TextListExtras::columnGap),
            Codec.INT.optionalFieldOf("lineLength", 150).forGetter(TextListExtras::lineLength),
            Codec.BOOL.optionalFieldOf("dropShadow", false).forGetter(TextListExtras::dropShadow),
            Codec.INT.optionalFieldOf("textColour", 0).forGetter(TextListExtras::textColour),
            Codec.STRING.optionalFieldOf("bullet_point", "▪").forGetter(TextListExtras::bulletPoint),
            Codec.BOOL.optionalFieldOf("underline_clickable", true).forGetter(TextListExtras::underlineClickable),
            Codec.BOOL.optionalFieldOf("centered", false).forGetter(TextListExtras::centered)
    ).apply(inst, TextListExtras::new));

    public static TextListExtras getDefault() {
        return new TextListExtras(0, 20, 45, 150, false, 0, "▪", true, false);
    }

}
