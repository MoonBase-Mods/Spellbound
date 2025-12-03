package com.ombremoon.spellbound.util;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.Tags;

public class EntityUtil {

    public static boolean isBoss(Entity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES);
    }
}
