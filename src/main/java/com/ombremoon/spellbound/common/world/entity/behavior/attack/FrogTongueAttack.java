package com.ombremoon.spellbound.common.world.entity.behavior.attack;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.world.entity.living.familiars.FrogEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class FrogTongueAttack extends ExtendedBehaviour<FrogEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.ATTACK_TARGET).noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);
    private LivingEntity target;
    private int endTime;
    private int doActionAt;

    public FrogTongueAttack() {
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, FrogEntity entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);

        return entity.getSensing().hasLineOfSight(this.target) && entity.isWithinMeleeAttackRange(this.target);
    }

    @Override
    protected boolean shouldKeepRunning(FrogEntity entity) {
        return this.endTime >= entity.tickCount;
    }

    @Override
    protected void start(FrogEntity entity) {
        entity.setTongueTarget(this.target);
        this.endTime = entity.tickCount + 10;
        this.doActionAt = entity.tickCount + 5;
    }

    @Override
    protected void tick(FrogEntity entity) {
        if (this.doActionAt == entity.tickCount) doDelayedAction(entity);
    }

    protected void doDelayedAction(FrogEntity entity) {
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, 20);

        if (this.target == null)
            return;

        if (!entity.getSensing().hasLineOfSight(this.target) || !entity.isWithinMeleeAttackRange(this.target))
            return;

        entity.setTongueTarget(this.target);
        entity.doHurtTarget(this.target);
    }

    @Override
    protected void stop(FrogEntity entity) {
        this.target = null;
        entity.eraseTongueTarget();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
