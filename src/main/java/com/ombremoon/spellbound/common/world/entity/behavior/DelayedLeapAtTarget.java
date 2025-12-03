package com.ombremoon.spellbound.common.world.entity.behavior;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class DelayedLeapAtTarget<E extends Mob> extends LeapAtTarget<E> {
    private final int jumpDelay;
    private int jumpDelayFinishedAt;

    public DelayedLeapAtTarget(int jumpDelay, int attackDelay) {
        super(attackDelay);
        this.jumpDelay = jumpDelay;
    }

    protected void jump(E entity) {
        Vec3 velocity = new Vec3(this.target.getX() - entity.getX(), 0, this.target.getZ() - entity.getZ());
        BehaviorUtils.lookAtEntity(entity, this.target);

        if (velocity.lengthSqr() > 1.0E-7)
            velocity = velocity.normalize().scale(this.jumpStrength.apply(entity, this.target)).add(entity.getDeltaMovement().scale(this.moveSpeedContribution.apply(entity, this.target)));

        entity.setDeltaMovement(velocity.x, this.verticalJumpStrength.apply(entity, this.target), velocity.z);
    }

    @Override
    protected void tick(E entity) {
        super.tick(entity);
        if (jumpDelay > 0 && jumpDelayFinishedAt == entity.tickCount) {
            jump(entity);
        }
    }

    @Override
    protected void start(E entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);
        entity.swing(InteractionHand.MAIN_HAND);
        BehaviorUtils.lookAtEntity(entity, this.target);
        BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);

        if (jumpDelay > 0) {
            this.jumpDelayFinishedAt = jumpDelay + entity.tickCount;
        } else jump(entity);
    }
}
