package com.ombremoon.spellbound.common.world.entity.behavior.sensor;

import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.init.SBSensors;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiPredicate;

public class OwnerAttackSenor<E extends LivingEntity> extends PredicateSensor<DamageSource, E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(SBMemoryTypes.OWNER_ATTACK.get(), SBMemoryTypes.OWNER_ATTACK_ENTITY.get());
    protected BiPredicate<E, LivingEntity> allyPredicate = (owner, ally) -> ally.isAlliedTo(owner);

    public OwnerAttackSenor() {
        super((damageSource, entity) -> true);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SBSensors.OWNER_ATTACK.get();
    }

    public OwnerAttackSenor<E> isAllyIf(BiPredicate<E, LivingEntity> predicate) {
        this.allyPredicate = predicate;

        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doTick(ServerLevel level, E entity) {
        Brain<?> brain = entity.getBrain();
        Entity owner = SpellUtil.getOwner(entity);

        if (!(owner instanceof LivingEntity livingEntity))
            return;

        LivingEntity target = livingEntity.getLastHurtMob();

        if (target == null || this.allyPredicate.test((E) livingEntity, target))
            return;

        DamageSource damageSource = target.getLastDamageSource();

        if (damageSource == null) {
            BrainUtils.clearMemory(brain, SBMemoryTypes.OWNER_ATTACK.get());
            BrainUtils.clearMemory(brain, SBMemoryTypes.OWNER_ATTACK_ENTITY.get());
        } else if (predicate().test(damageSource, entity)) {
            BrainUtils.setMemory(brain, SBMemoryTypes.OWNER_ATTACK.get(), damageSource);

            if (target.isAlive() && target.level() == entity.level())
                BrainUtils.setMemory(brain, SBMemoryTypes.OWNER_ATTACK_ENTITY.get(), target);
        } else {
            BrainUtils.withMemory(brain, SBMemoryTypes.OWNER_ATTACK_ENTITY.get(), targetEntity -> {
                if (!targetEntity.isAlive() || targetEntity.level() != entity.level())
                    BrainUtils.clearMemory(brain, SBMemoryTypes.OWNER_ATTACK_ENTITY.get());
            });
        }
    }
}
