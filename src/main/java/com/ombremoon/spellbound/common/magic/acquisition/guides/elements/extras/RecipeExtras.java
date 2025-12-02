package com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public record RecipeExtras(ResourceLocation scrap) implements IElementExtra {
    public static final Codec<RecipeExtras> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(RecipeExtras::scrap)
            ).apply(inst, RecipeExtras::new));

    public static RecipeExtras getDefault() {
        return new RecipeExtras(CommonClass.customLocation("default"));
    }


}
