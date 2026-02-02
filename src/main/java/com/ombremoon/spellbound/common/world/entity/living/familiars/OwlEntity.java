package com.ombremoon.spellbound.common.world.entity.living.familiars;

import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.entity.SmartSpellEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.List;

public class OwlEntity extends SBLivingEntity {
    protected OwlEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return List.of();
    }
}
