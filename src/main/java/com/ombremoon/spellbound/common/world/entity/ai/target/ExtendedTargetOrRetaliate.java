package com.ombremoon.spellbound.common.world.entity.ai.target;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtendedTargetOrRetaliate<E extends SBLivingEntity> extends TargetOrRetaliate<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(6).usesMemories(SBMemoryTypes.OWNER_ATTACK.get(), SBMemoryTypes.HURT_OWNER_ENTITY.get(), MemoryModuleType.ATTACK_TARGET, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

    public ExtendedTargetOrRetaliate() {
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected @Nullable LivingEntity getTarget(E owner, ServerLevel level, @Nullable LivingEntity existingTarget) {
        Brain<?> brain = owner.getBrain();
        LivingEntity newTarget = BrainUtils.getMemory(brain, this.priorityTargetMemory);

        if (newTarget == null) {
            newTarget = BrainUtils.getMemory(brain, MemoryModuleType.HURT_BY_ENTITY);

            if (newTarget == null) {
                newTarget = BrainUtils.getMemory(brain, SBMemoryTypes.OWNER_ATTACK_ENTITY.get());

                if (newTarget == null) {
                    newTarget = BrainUtils.getMemory(brain, SBMemoryTypes.HURT_OWNER_ENTITY.get());

                    if (newTarget == null) {
                        NearestVisibleLivingEntities nearbyEntities = BrainUtils.getMemory(brain, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

                        if (nearbyEntities != null)
                            newTarget = nearbyEntities.findClosest(this.canAttackPredicate).orElse(null);

                        if (newTarget == null)
                            return null;
                    }
                }
            }
        }

        if (newTarget == existingTarget)
            return null;

        return this.canAttackPredicate.test(newTarget) && !owner.isAlliedTo(newTarget) ? newTarget : null;
    }
}
