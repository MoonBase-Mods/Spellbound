package com.ombremoon.spellbound.common.init;

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
    public static final ResourceLocation BLESS_SHRINE_LORE = scrap("bless_shrine", SpellboundToasts.DIVINE);
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

    public enum SpellboundToasts {
        DECEPTION("deception_scrap_toast"),
        DIVINE("divine_scrap_toast"),
        RUIN("ruin_scrap_toast"),
        SUMMON("summon_scrap_toast"),
        TRANSFIG("transfig_scrap_toast");

        private final String textureName;

        SpellboundToasts(String texture) {
            this.textureName = texture;
        }

        public ResourceLocation getTexture() {
            return CommonClass.customLocation("textures/gui/toasts/" + textureName + ".png");
        }
    }
}
