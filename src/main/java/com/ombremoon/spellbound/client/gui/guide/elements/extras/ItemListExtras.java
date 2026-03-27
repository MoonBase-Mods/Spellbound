package com.ombremoon.spellbound.client.gui.guide.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

public record ItemListExtras(ResourceLocation pageScrap, int maxColumns, int rowGap, int columnGap, int countGap, boolean dropShadow, int textColour, boolean centered) implements IElementExtra {
    public static final Codec<ItemListExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(ItemListExtras::pageScrap),
            Codec.INT.optionalFieldOf("maxColumns", 0).forGetter(ItemListExtras::maxColumns),
            Codec.INT.optionalFieldOf("rowGap", 4).forGetter(ItemListExtras::rowGap),
            Codec.INT.optionalFieldOf("columnGap", 6).forGetter(ItemListExtras::columnGap),
            Codec.INT.optionalFieldOf("countGap", 33).forGetter(ItemListExtras::countGap),
            Codec.BOOL.optionalFieldOf("dropShadow", false).forGetter(ItemListExtras::dropShadow),
            Codec.INT.optionalFieldOf("textColour", 0).forGetter(ItemListExtras::textColour),
            Codec.BOOL.optionalFieldOf("centered", false).forGetter(ItemListExtras::centered)
    ).apply(inst, ItemListExtras::new));

    public static ItemListExtras getDefault() {
        return new ItemListExtras(CommonClass.customLocation("default"), 0, 4, 6, 33, true, 0, false);
    }


}
