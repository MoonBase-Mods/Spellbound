package com.ombremoon.spellbound.common.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.EntityGetter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface SBSummonable extends OwnableEntity {
    boolean isSummoner(LivingEntity entity);

    boolean hasSummoner();

    boolean wasSummoned();

    Entity getSummoner();

    default void setSummoner(Entity entity) {
        this.setSummoner(entity.getId());
    }

    void setSummoner(int id);

    @Override
    @Nullable default UUID getOwnerUUID() {
        Entity entity = this.getSummoner();
        return entity == null ? null : entity.getUUID();
    }
}
