package com.ombremoon.spellbound.common.world.entity.living.familiars;

import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;

public class CatEntity extends SBFamiliarEntity {
    private boolean canPounce = false;

    public CatEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget(),
                new SequentialBehaviour<>(
                        new SetWalkTargetToAttackTarget<>(),
                        new LeapAtTarget<CatEntity>(20)
                                .leapRange(((mob, livingEntity) -> 8f))
                                .startCondition(CatEntity::canPounce)
                                .whenStopping(CatEntity::disablePounce),
                        new LeapAtTarget<>(20)
                                .leapRange(((mob, livingEntity) -> 4f))
                )
        );
    }

    public boolean canPounce() {
        return canPounce;
    }

    public void disablePounce() {
        this.canPounce = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        if (!canPounce && this.brain.getActiveNonCoreActivity().isPresent() && this.brain.getActiveNonCoreActivity().get() == Activity.IDLE) {
            Entity owner = getSummoner();
            if (owner instanceof LivingEntity summoner && isFamiliar() && !getFamiliar().hasAffinity(SpellUtil.getFamiliarHandler(summoner), SBAffinities.FELINE_POUNCE))
                this.canPounce = false;

            this.canPounce = true;
        }
    }

    @Override
    public float modifyDamage(Entity target, float attackDamage, Familiar<?> familiar, FamiliarHandler familiarHandler) {
        if (familiar == null || !(target instanceof LivingEntity livingTarget)) return attackDamage;

        if (familiar.hasAffinity(familiarHandler, SBAffinities.BLOOD_THIRSTY) && livingTarget.hasEffect(SBEffects.BLEED))
            attackDamage *= 1.1f;

        return attackDamage;
    }

    @Override
    public DamageSource getDamageSource(DamageSources sources) {
        if (!(getSummoner() instanceof LivingEntity summoner) || getFamiliar() == null) return super.getDamageSource(sources);

        if (getFamiliar().hasAffinity(SpellUtil.getFamiliarHandler(summoner), SBAffinities.SHARPENED_CLAWS))
            return sources.source(SBDamageTypes.BLEED);

        return super.getDamageSource(sources);
    }

    @Override
    public float getIdleScale() {
        return 0.4f;
    }

    @Override
    public Vec3 getRidingOffset() {
        return new Vec3(0.5D, 0.3D, 0.1D);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return entity.distanceTo(this) <= 3f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, (double)10.0F)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, (double)3.0F);
    }
}
