package com.ombremoon.spellbound.common.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface SBSummonable {
    boolean isSummoner(LivingEntity entity);

    boolean hasSummoner();

    boolean wasSummoned();

    Entity getSummoner();

    default void setSummoner(Entity entity) {
        this.setSummoner(entity.getId());
    }

    void setSummoner(int id);
}
