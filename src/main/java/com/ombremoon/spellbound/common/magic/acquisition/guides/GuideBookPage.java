package com.ombremoon.spellbound.common.magic.acquisition.guides;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.IPageElement;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record GuideBookPage(ResourceLocation id, ResourceLocation pageScrap, ResourceLocation insertAfter, List<IPageElement> elements) {
    public static final Codec<GuideBookPage> CODEC = RecordCodecBuilder.<GuideBookPage>create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(GuideBookPage::id),
            ResourceLocation.CODEC.optionalFieldOf("pageScrap", CommonClass.customLocation("default")).forGetter(GuideBookPage::pageScrap),
            ResourceLocation.CODEC.optionalFieldOf("insertAfter", CommonClass.customLocation("default")).forGetter(GuideBookPage::insertAfter),
            IPageElement.CODEC.listOf().optionalFieldOf("elements", new ArrayList<>()).forGetter(GuideBookPage::elements)
    ).apply(inst, GuideBookPage::new));


}
