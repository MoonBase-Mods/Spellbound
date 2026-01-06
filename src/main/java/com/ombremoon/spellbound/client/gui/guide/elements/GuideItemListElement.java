package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ItemListExtras;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IInteractable;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GuideItemListElement(List<ItemListEntry> items, ItemListExtras extras, ElementPosition position) implements IPageElement, IInteractable, IHoverable {
    public static final MapCodec<GuideItemListElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemListEntry.CODEC.listOf().fieldOf("items").forGetter(GuideItemListElement::items),
            ItemListExtras.CODEC.optionalFieldOf("extras", ItemListExtras.getDefault()).forGetter(GuideItemListElement::extras),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideItemListElement::position)
    ).apply(inst, GuideItemListElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }


    public record ItemListEntry(List<ItemStack> items, NumberProvider count, ResourceLocation scrap) {
        public static final Codec<ItemListEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ItemStack.CODEC.listOf().fieldOf("items").forGetter(ItemListEntry::items),
                NumberProviders.CODEC.optionalFieldOf("count", ConstantValue.exactly(1)).forGetter(ItemListEntry::count),
                ResourceLocation.CODEC.optionalFieldOf("scrap", GuideBookManager.FIRST_PAGE).forGetter(ItemListEntry::scrap)
        ).apply(inst, ItemListEntry::new));
    }
}
