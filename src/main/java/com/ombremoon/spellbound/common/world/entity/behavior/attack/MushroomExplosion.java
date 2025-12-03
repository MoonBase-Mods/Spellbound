package com.ombremoon.spellbound.common.world.entity.behavior.attack;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.world.entity.living.wildmushroom.LivingMushroom;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class MushroomExplosion<E extends LivingMushroom> extends DelayedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.ATTACK_TARGET).noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);

    @Nullable
    protected LivingEntity target = null;
    protected Function<E, Float> attackRadius = mushroom -> 5.0F;
    protected Function<E, Float> explosionRadius = mushroom -> 3.0F;
    protected Function<E, Float> explosionDamage = mushroom -> 4.0F;
    protected Function<E, String> explosionAnimation = mushroom -> "explode";
    protected Function<E, Integer> attackIntervalSupplier = mushroom -> mushroom.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
    protected boolean shouldDiscard = false;

    public MushroomExplosion(int delayTicks) {
        super(delayTicks);
    }

    public MushroomExplosion<E> attackRadius(Function<E, Float> radius) {
        this.attackRadius = radius;

        return this;
    }

    public MushroomExplosion<E> explosionDamage(Function<E, Float> damage) {
        this.explosionDamage = damage;

        return this;
    }

    public MushroomExplosion<E> explosionRadius(Function<E, Float> radius) {
        this.explosionRadius = radius;

        return this;
    }

    public MushroomExplosion<E> explosionAnimation(Function<E, String> animation) {
        this.explosionAnimation = animation;

        return this;
    }

    public MushroomExplosion<E> attackInterval(Function<E, Integer> supplier) {
        this.attackIntervalSupplier = supplier;

        return this;
    }

    public MushroomExplosion<E> shouldDiscard() {
        this.shouldDiscard = true;

        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);

        float radius = this.explosionRadius.apply(entity) + entity.getBbWidth();
        return entity.getSensing().hasLineOfSight(this.target) && entity.distanceToSqr(this.target) <= radius * radius;
    }

    @Override
    protected void start(E entity) {
        entity.triggerAnim("explosion", this.explosionAnimation.apply(entity));
        entity.setExploding(true);
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
    }

    @Override
    protected void stop(E entity) {
        entity.setExploding(false);
    }

    @Override
    protected void doDelayedAction(E entity) {
        if (this.target == null)
            return;

        AbstractSpell spell = entity.getSpell();
        boolean flag = spell != null;
        float radius = this.explosionRadius.apply(entity);
        for (LivingEntity target : EntityRetrievalUtil.getEntities(entity, radius, radius, radius, LivingEntity.class, livingEntity -> !livingEntity.isAlliedTo(entity))) {
            if (flag && spell.hurt(entity, target) || entity.hurtTarget(target, entity.spellDamageSource(entity.level()), this.explosionDamage.apply(entity))) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
            }
        }

        if (this.shouldDiscard) {
            entity.discard();
            if (flag)
                spell.endSpell();
        }
    }
}
