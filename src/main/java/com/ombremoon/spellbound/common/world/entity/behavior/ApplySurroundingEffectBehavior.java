package com.ombremoon.spellbound.common.world.entity.behavior;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.world.entity.SmartSpellEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ApplySurroundingEffectBehavior<E extends SmartSpellEntity> extends ExtendedBehaviour<E> {
    protected Function<E, AABB> effectArea = Entity::getBoundingBox;
    protected Predicate<LivingEntity> applyPredicate = livingEntity -> true;
    protected MobEffectInstance mobEffect;

    public ApplySurroundingEffectBehavior(MobEffectInstance effect) {
        this.mobEffect = effect;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of();
    }

    public ApplySurroundingEffectBehavior<E> areaOf(Function<E, AABB> effectArea) {
        this.effectArea = effectArea;

        return this;
    }

    public ApplySurroundingEffectBehavior<E> applyPredicate(Predicate<LivingEntity> predicate) {
        this.applyPredicate = predicate;

        return this;
    }

    @Override
    protected void start(E entity) {
        List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, effectArea.apply(entity), applyPredicate);
        entities.forEach(e -> e.addEffect(mobEffect)); //Honestly no clue if u can reuse mob effect instances like this something tells me no for some reason
    }
}
