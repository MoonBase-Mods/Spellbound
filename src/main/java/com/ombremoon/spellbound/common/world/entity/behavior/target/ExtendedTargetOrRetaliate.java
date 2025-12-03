package com.ombremoon.spellbound.common.world.entity.behavior.target;

import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class ExtendedTargetOrRetaliate<E extends Mob> extends TargetOrRetaliate<E> {

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

        return this.canAttackPredicate.test(newTarget) ? newTarget : null;
    }
}
