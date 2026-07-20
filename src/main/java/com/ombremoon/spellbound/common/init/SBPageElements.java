package com.ombremoon.spellbound.common.init;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.client.gui.guide.elements.*;
import com.ombremoon.spellbound.client.gui.guide.elements.TransfigurationRitualElement;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SBPageElements {
    public static final ResourceKey<Registry<MapCodec<? extends IPageElement>>> PAGE_ELEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("page_element"));
    public static final Registry<MapCodec<? extends IPageElement>> REGISTRY = new RegistryBuilder<>(PAGE_ELEMENT_REGISTRY_KEY).create();
    public static final DeferredRegister<MapCodec<? extends IPageElement>> PAGE_ELEMENTS = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    public static final Supplier<MapCodec<? extends IPageElement>> TEXT = PAGE_ELEMENTS.register("text", () -> GuideTextElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> TOOLTIP = PAGE_ELEMENTS.register("tooltip", () -> GuideTooltipElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> IMAGE = PAGE_ELEMENTS.register("image", () -> GuideImageElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> ENTITY_RENDERER = PAGE_ELEMENTS.register("entity_renderer", () -> GuideEntityElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> ITEM = PAGE_ELEMENTS.register("item", () -> GuideStaticItemElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> RECIPE = PAGE_ELEMENTS.register("recipe", () -> GuideRecipeElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> ITEM_LIST = PAGE_ELEMENTS.register("item_list", () -> GuideItemListElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> ITEM_RENDERER = PAGE_ELEMENTS.register("item_renderer", () -> GuideItemElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> SPELL_INFO = PAGE_ELEMENTS.register("spell_info", () -> GuideSpellInfoElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> TEXT_LIST = PAGE_ELEMENTS.register("text_list", () -> GuideTextListElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> SPELL_BORDER = PAGE_ELEMENTS.register("spell_border", () -> GuideSpellBorderElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> RITUAL = PAGE_ELEMENTS.register("ritual", () -> TransfigurationRitualElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> EQUIPMENT = PAGE_ELEMENTS.register("equipment", () -> GuideEquipmentElement.CODEC);
    public static final Supplier<MapCodec<? extends IPageElement>> STRUCTURE = PAGE_ELEMENTS.register("structure", () -> GuideMultiBlockElement.CODEC);

    public static void register(IEventBus eventBus) {
        PAGE_ELEMENTS.register(eventBus);
    }
}
