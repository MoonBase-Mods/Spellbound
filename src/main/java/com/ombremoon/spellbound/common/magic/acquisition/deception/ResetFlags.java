package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ResetFlags {
    private static final List<ResourceLocation> FLAGS = new ArrayList<>();
    public static final ResourceLocation RESET_POSITION = makeFlag("reset_position");
    public static final ResourceLocation RESET_INVENTORY = makeFlag("reset_inventory");

    public static boolean isFlag(ResourceLocation rule) {
        return FLAGS.contains(rule);
    }

    //Add registration event
    private static ResourceLocation makeFlag(String key) {
        ResourceLocation rule = CommonClass.customLocation(key);
        if (FLAGS.contains(rule))
            throw new IllegalStateException("Duplicate flag registration for " + key);

        FLAGS.add(rule);
        return rule;
    }
}
