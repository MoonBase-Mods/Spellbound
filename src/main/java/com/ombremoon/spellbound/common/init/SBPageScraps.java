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
    public static final ResourceLocation HEAL_MOB_TO_FULL = scrap("heal_mob_to_full", SpellboundToasts.DIVINE);
    public static final ResourceLocation HEAL_MOB_TO_FULL_LORE = scrap("heal_mob_to_full_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_BLESSED_BANDAGES = scrap("use_blessed_bandages", SpellboundToasts.DIVINE);
    public static final ResourceLocation USE_BLESSED_BANDAGES_LORE = scrap("use_blessed_bandages_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation BLESS_SHRINE = scrap("bless_shrine", SpellboundToasts.DIVINE);
    public static final ResourceLocation BLESS_SHRINE_LORE = scrap("bless_shrine_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation DECORATE_SHRINE = scrap("decorate_shrine", SpellboundToasts.DIVINE);
    public static final ResourceLocation DECORATE_SHRINE_LORE = scrap("decorate_shrine_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURE_VILLAGER = scrap("cure_villager", SpellboundToasts.DIVINE);
    public static final ResourceLocation CURE_VILLAGER_LORE = scrap("cure_villager_lore", SpellboundToasts.DIVINE);
    public static final ResourceLocation KILL_VILLAGER = scrap("kill_villager", SpellboundToasts.DIVINE);
    public static final ResourceLocation KILL_VILLAGER_LORE = scrap("kill_villager_lore", SpellboundToasts.DIVINE);
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
