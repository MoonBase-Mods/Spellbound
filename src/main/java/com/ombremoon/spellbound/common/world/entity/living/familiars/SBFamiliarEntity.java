package com.ombremoon.spellbound.common.world.entity.living.familiars;

import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarContext;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

public abstract class SBFamiliarEntity extends SBLivingEntity {
    public static final EntityDataAccessor<Boolean> IS_IDLE = SynchedEntityData.defineId(SBFamiliarEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean isFamiliar = false;

    protected SBFamiliarEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public void markFamiliar() {
        isFamiliar = true;
    }

    public boolean isFamiliar() {
        return isFamiliar;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_IDLE, true);
    }

    public void setIdle(boolean idle) {
        this.getEntityData().set(IS_IDLE, idle);
    }

    public boolean isIdle() {
        return this.getEntityData().get(IS_IDLE);
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !ridingSummoner();
    }

    @Override
    public float getScale() {
        return this.ridingSummoner() ? getIdleScale() : super.getScale();
    }

    public float getIdleScale() {
        return 0.5F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (ridingSummoner()) return false;
        return super.hurt(source, amount);
    }

    public boolean ridingSummoner() {
        return this.isPassenger() && this.getVehicle().is(this.getSummoner());
    }

    @Override
    public void tick() {
        super.tick();

        if (isFamiliar() && getSummoner() instanceof LivingEntity summoner) {
            LivingEntity activeFam = SpellUtil.getFamiliarHandler(summoner).getActiveEntity();
            if (activeFam == null || !activeFam.is(this)) {
                discard();
            }
        }

        if (!level().isClientSide()) {
            boolean isBrainIdle = this.brain.getActiveNonCoreActivity().isPresent() && this.brain.getActiveNonCoreActivity().get() == Activity.IDLE;
            if (isBrainIdle && !isIdle())
                this.setIdle(true);
            else if (!isBrainIdle && isIdle())
                this.setIdle(false);
        }

        if (isIdle() && !ridingSummoner())
            this.startRiding(getSummoner());
        else if (!isIdle() && ridingSummoner()) {
            this.stopRiding();
            LivingEntity summoner = (LivingEntity) getSummoner();
            Vec3 right = summoner.getLookAngle()
                    .cross(new Vec3(0, 1, 0))
                    .normalize()
                    .scale(2.0);

            this.setPos(
                    summoner.getX() + right.x,
                    summoner.getY(),
                    summoner.getZ() + right.z
            );
        }

        if (ridingSummoner() && getSummoner() instanceof LivingEntity summoner) {
            this.setRot(summoner.getYRot(), summoner.getXRot());
            this.setYHeadRot(summoner.getYHeadRot());
        }
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity entity) {

        Vec3 offset = getRidingOffset();
        double rot = Math.toRadians(entity.getYRot());

        double x =  offset.x * Math.cos(rot) - offset.z * Math.sin(rot);
        double z =  offset.x * Math.sin(rot) + offset.z * Math.cos(rot);

        return new Vec3(x, offset.y, z);
    }

    public Vec3 getRidingOffset() {
        return new Vec3(1, 0, 0);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return List.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public BrainActivityGroup<? extends SBLivingEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour(
                        new TargetOrRetaliate<SBFamiliarEntity>()
                                .attackablePredicate(summonAttackPredicate())
                )
        );
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damagesource = this.damageSources().source(SBDamageTypes.SB_GENERIC);
        if (level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        boolean flag = entity.hurt(damagesource, f);
        if (flag) {
            if (getSummoner() instanceof LivingEntity summoner) {
                var handler = SpellUtil.getFamiliarHandler(summoner);
                handler.awardBond(handler.getActivatedFamiliarHolder(), calculateHurtXP(f));
            }
            float f1 = this.getKnockback(entity, damagesource);
            if (f1 > 0.0F && entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                livingentity.knockback((double)(f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, (double)1.0F, 0.6));
            }

            if (level() instanceof ServerLevel server) {
                ServerLevel serverlevel1 = server;
                EnchantmentHelper.doPostAttackEffects(serverlevel1, entity, damagesource);
            }

            this.setLastHurtMob(entity);
            this.playAttackSound();
        }

        return flag;
    }

    /**
     * Calculates the amount of XP gained from hurting an entity
     * @param amount The damage amount
     * @return The modified xp value
     */
    private float calculateHurtXP(float amount) {
        return amount * (1.0F + Familiar.HURT_XP_MODIFIER);
    }
}
