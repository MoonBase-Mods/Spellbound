package com.ombremoon.spellbound.common.world.entity.ai.goal;

import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class SummonerAttackGoal extends TargetGoal {
    private static final TargetingConditions SUMMONER_ATTACK_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;

    public SummonerAttackGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, false);
        this.toIgnoreDamage = toIgnoreDamage;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Entity entity = SpellUtil.getOwner(this.mob);
        if (entity instanceof LivingEntity owner) {
            int i = owner.getLastHurtMobTimestamp();
            LivingEntity livingentity = owner.getLastHurtMob();
            if (i != this.timestamp && livingentity != null) {
                if (SpellUtil.IS_ALLIED.test(livingentity, owner)) {
                    return false;
                } else {
                    for (Class<?> oclass : this.toIgnoreDamage) {
                        if (oclass.isAssignableFrom(entity.getClass())) {
                            return false;
                        }
                    }

                    return this.canAttack(livingentity, SUMMONER_ATTACK_TARGETING);
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
        this.mob.setTarget(owner.getLastHurtMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = owner.getLastHurtMobTimestamp();
        this.unseenMemoryTicks = 300;
        super.start();
    }
}
