package com.ombremoon.spellbound.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.Tags;

public class EntityUtil {

    public static boolean isBoss(Entity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES);
    }

    public static boolean isMoving(LivingEntity entity) {
        return entity.xo != entity.getX() || entity.yo != entity.getY() || entity.zo != entity.getZ();
    }
}
