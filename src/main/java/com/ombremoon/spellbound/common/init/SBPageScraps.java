package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.client.gui.toasts.SpellboundToasts;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SBPageScraps {
    private static final Map<ResourceLocation, ResourceLocation> TEXTURES = new HashMap<>();

    public static final ResourceLocation DEFAULT = scrap("default", SpellboundToasts.RUIN);
    public static final ResourceLocation UNLOCKED_SOLAR_RAY = scrap("solar_ray_unlock", SpellboundToasts.RUIN);
    public static final ResourceLocation USE_BLESSED_BANDAGES = scrap("use_blessed_bandages", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_BLESSED_BANDAGES_LORE = scrap("use_blessed_bandages_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_PHIAL = scrap("use_phial", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_PHIAL_LORE = scrap("use_phial_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation BLESS_SHRINE = scrap("bless_shrine", SpellboundToasts.DIVINE);
    public static final ResourceLocation BLESS_SHRINE_LORE = scrap("bless_shrine_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURE_VILLAGER = scrap("cure_villager", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURE_VILLAGER_LORE = scrap("cure_villager_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation BESTOW_CHARM = scrap("bestow_charm", SpellboundToasts.DIVINE);
    public static final ResourceLocation BESTOW_CHARM_LORE = scrap("bestow_charm_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation ANOINT_WEAPON = scrap("anoint_weapon", SpellboundToasts.DIVINE);
    public static final ResourceLocation ANOINT_WEAPON_LORE = scrap("anoint_weapon_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation DEFLECT_UNDEAD_ARROW = scrap("deflect_undead_arrow", SpellboundToasts.DIVINE);
    public static final ResourceLocation DEFLECT_UNDEAD_ARROW_LORE = scrap("deflect_undead_arrow_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURSE_WEAPON = scrap("curse_weapon", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURSE_WEAPON_LORE = scrap("curse_weapon_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURSE_ENEMY_WITH_PHIAL = scrap("curse_enemy_with_phial", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURSE_ENEMY_WITH_PHIAL_LORE = scrap("curse_enemy_with_phial_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_HALF_FILLED_SIPHON = scrap("use_half_filled_siphon", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_HALF_FILLED_SIPHON_LORE = scrap("use_half_filled_siphon_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURSE_SHRINE = scrap("curse_shrine", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURSE_SHRINE_LORE = scrap("curse_shrine_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation DECORATE_SHRINE = scrap("decorate_shrine", SpellboundToasts.DIVINE);
    public static final ResourceLocation DECORATE_SHRINE_LORE = scrap("decorate_shrine_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation GROW_AMBROSIA_BUSH = scrap("grow_ambrosia_bush", SpellboundToasts.DIVINE);
    public static final ResourceLocation GROW_AMBROSIA_BUSH_LORE = scrap("grow_ambrosia_bush_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation PURIFY_WITHER_ROSE = scrap("purify_wither_rose", SpellboundToasts.DIVINE);
    public static final ResourceLocation PURIFY_WITHER_ROSE_LORE = scrap("purify_wither_rose_lore", SpellboundToasts.DIVINE);

    public static ResourceLocation scrap(String path, SpellboundToasts toast) {
        return registerToast(CommonClass.customLocation(path), toast.getTexture());
    }

    public static ResourceLocation registerToast(ResourceLocation scrap, ResourceLocation texture) {
        TEXTURES.put(scrap, texture);
        return scrap;
    }

    public static ResourceLocation getTexture(ResourceLocation scrap) {
        return TEXTURES.get(scrap);
    }


}
