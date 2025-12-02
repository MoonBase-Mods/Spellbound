package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ItemExtras;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record GuideItem(ResourceLocation itemLoc, String tileName, float scale, ElementPosition position, ItemExtras extras) implements IPageElement {
    public static final MapCodec<GuideItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(GuideItem::itemLoc),
            Codec.STRING.optionalFieldOf("tileName", "basic").forGetter(GuideItem::tileName),
            Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(GuideItem::scale),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideItem::position),
            ItemExtras.CODEC.optionalFieldOf("extras", ItemExtras.getDefault()).forGetter(GuideItem::extras)
    ).apply(inst, GuideItem::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
