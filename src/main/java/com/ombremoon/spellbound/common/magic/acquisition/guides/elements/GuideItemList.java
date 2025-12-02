package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ItemListExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GuideItemList(List<ItemListEntry> items, ItemListExtras extras, ElementPosition position) implements IPageElement {
    public static final MapCodec<GuideItemList> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemListEntry.CODEC.listOf().fieldOf("items").forGetter(GuideItemList::items),
            ItemListExtras.CODEC.optionalFieldOf("extras", ItemListExtras.getDefault()).forGetter(GuideItemList::extras),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideItemList::position)
    ).apply(inst, GuideItemList::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }


    public record ItemListEntry(ResourceLocation itemLoc, int count) {
        public static final Codec<ItemListEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(ItemListEntry::itemLoc),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ItemListEntry::count)
        ).apply(inst, ItemListEntry::new));
    }
}
