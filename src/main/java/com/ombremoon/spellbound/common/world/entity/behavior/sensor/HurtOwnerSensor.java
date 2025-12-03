package com.ombremoon.spellbound.common.world.entity.behavior.sensor;

import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.init.SBSensors;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class HurtOwnerSensor<E extends Mob> extends PredicateSensor<DamageSource, E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(SBMemoryTypes.HURT_OWNER.get(), SBMemoryTypes.HURT_OWNER_ENTITY.get());

    public HurtOwnerSensor() {
        super((damageSource, entity) -> true);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SBSensors.HURT_OWNER.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        Brain<?> brain = entity.getBrain();
        Entity owner = SpellUtil.getOwner(entity);

        if (!(owner instanceof LivingEntity livingEntity))
            return;

        DamageSource damageSource = livingEntity.getLastDamageSource();

        if (damageSource == null) {
            BrainUtils.clearMemory(brain, SBMemoryTypes.HURT_OWNER.get());
            BrainUtils.clearMemory(brain, SBMemoryTypes.HURT_OWNER_ENTITY.get());
        } else if (predicate().test(damageSource, entity)) {
            BrainUtils.setMemory(brain, SBMemoryTypes.HURT_OWNER.get(), damageSource);

            if (damageSource.getEntity() instanceof LivingEntity attacker && !attacker.is(owner) && attacker.isAlive() && attacker.level() == entity.level())
                BrainUtils.setMemory(brain, SBMemoryTypes.HURT_OWNER_ENTITY.get(), attacker);
        } else {
            BrainUtils.withMemory(brain, SBMemoryTypes.HURT_OWNER_ENTITY.get(), attacker -> {
                if (!attacker.isAlive() || attacker.level() != entity.level())
                    BrainUtils.clearMemory(brain, SBMemoryTypes.HURT_OWNER_ENTITY.get());
            });
        }
    }
}
