package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class DungeonRules {
    private static List<ResourceLocation> RULES = new ArrayList<>();
    public static ResourceLocation NO_BUILDING = makeRule("no_building");
    public static ResourceLocation NO_INTERACT = makeRule("no_interact");
    public static ResourceLocation NO_PVP = makeRule("no_pvp");
    public static ResourceLocation NO_PVE = makeRule("no_pve");
    public static ResourceLocation NO_PVE_OR_PVP = makeRule("no_pve_or_pvp");
    public static ResourceLocation NO_FLYING = makeRule("no_flying");
    public static ResourceLocation NO_SPELL_CASTING = makeRule("no_spell_casting");

    public static boolean isRule(ResourceLocation rule) {
        return RULES.contains(rule);
    }

    //Add registration event
    private static ResourceLocation makeRule(String key) {
        ResourceLocation rule = CommonClass.customLocation(key);
        if (RULES.contains(rule))
            throw new IllegalStateException("Duplicate dungeon rule registration for " + key);

        RULES.add(rule);
        return rule;
    }
}
