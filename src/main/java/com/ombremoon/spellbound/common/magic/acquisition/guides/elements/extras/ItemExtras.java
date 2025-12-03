package com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public record ItemExtras(ResourceLocation pageScrap) implements IElementExtra {
    public static final Codec<ItemExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(ItemExtras::pageScrap)
    ).apply(inst, ItemExtras::new));

    public static ItemExtras getDefault() {
        return new ItemExtras(CommonClass.customLocation("default"));
    }


}
