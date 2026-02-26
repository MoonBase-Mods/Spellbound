package com.ombremoon.spellbound.common.world.entity.ai.goal;

import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class HurtSummonerGoal extends TargetGoal {
    private static final TargetingConditions HURT_SUMMONER_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;

    public HurtSummonerGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, false);
        this.toIgnoreDamage = toIgnoreDamage;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Entity entity = SpellUtil.getOwner(this.mob);
        if (entity instanceof LivingEntity owner) {
            int i = owner.getLastHurtByMobTimestamp();
            LivingEntity livingentity = owner.getLastHurtByMob();
            if (i != this.timestamp && livingentity != null) {
                if (SpellUtil.IS_ALLIED.test(owner, livingentity)) {
                    return false;
                } else {
                    for (Class<?> oclass : this.toIgnoreDamage) {
                        if (oclass.isAssignableFrom(livingentity.getClass())) {
                            return false;
                        }
                    }

                    return this.canAttack(livingentity, HURT_SUMMONER_TARGETING);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        LivingEntity owner = (LivingEntity) SpellUtil.getOwner(this.mob);
        this.mob.setTarget(owner.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = owner.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        super.start();
    }
}
