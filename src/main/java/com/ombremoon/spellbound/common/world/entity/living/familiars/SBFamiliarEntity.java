package com.ombremoon.spellbound.common.world.entity.living.familiars;

import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.magic.BuildUpEffect;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

public abstract class SBFamiliarEntity extends SBLivingEntity {
    private static final EntityDataAccessor<Byte> FAMILIAR_DATA = SynchedEntityData.defineId(SBFamiliarEntity.class, EntityDataSerializers.BYTE);
    private Familiar<?> familiar;

    protected SBFamiliarEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Yes i did make this a bit flag system just cause i wanted to see how hard they are to do. Conclusion: very easy
     * Flags: 1 = familiar, 2 = idle
     * @param i flag to set
     * @param active whether to mark as true/false
     */
    public void setData(int i, boolean active) {
        if (active)
            this.entityData.set(FAMILIAR_DATA, (byte) (this.entityData.get(FAMILIAR_DATA) | i));
        else
            this.entityData.set(FAMILIAR_DATA, (byte) (this.entityData.get(FAMILIAR_DATA) & ~i));
    }

    public void setFamiliar(Familiar<?> familiar) {
        this.familiar = familiar;
    }

    public Familiar<?> getFamiliar() {
        return familiar;
    }

    /**
     * Yes i did make this a bit flag system just cause i wanted to see how hard they are to do. Conclusion: very easy
     * Flags: 1 = familiar, 2 = idle
     * @param i flag to set
     * @return If the flag is set as true
     */
    public boolean getData(int i) {
        return (this.entityData.get(FAMILIAR_DATA) & i) != 0;
    }

    public void markFamiliar() {
        setData(1, true);
    }

    public boolean isFamiliar() {
        return getData(1);
    }

    public void setIdle(boolean idle) {
        setData(2, idle);
    }

    public boolean isIdle() {
        return getData(2);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FAMILIAR_DATA, (byte) 0);
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

        if (isFamiliar()) {
            Entity summonerEntity = getSummoner();
            if (!(summonerEntity instanceof LivingEntity summoner)) {
                discard();
                return;
            }

            var handler = SpellUtil.getFamiliarHandler(summoner);
            LivingEntity activeFam = handler.getActiveEntity();
            if (activeFam == null || !activeFam.is(this)) {
                discard();
                return;
            }
        } else if (getVehicle() instanceof Player) discard();

        if (!level().isClientSide()) {
            boolean isBrainIdle = this.brain.getActiveNonCoreActivity().isPresent() && this.brain.getActiveNonCoreActivity().get() == Activity.IDLE;
            if (isBrainIdle && !isIdle())
                this.setIdle(true);
            else if (!isBrainIdle && isIdle())
                this.setIdle(false);
        }

        if (getSummoner() == null) {
          this.discard();
          return;
        } else if (isIdle() && !ridingSummoner())
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
        return new Vec3(0.5, 0, 0);
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

    public float modifyDamage(Entity target, float attackDamage, Familiar<?> familiar, FamiliarHandler familiarHandler) {
        return attackDamage;
    }

    public DamageSource getDamageSource(DamageSources sources) {
        return sources.source(SBDamageTypes.SB_GENERIC);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = modifyDamage(
                entity,
                (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE),
                getFamiliar(),
                getSummoner() instanceof LivingEntity summoner ? SpellUtil.getFamiliarHandler(summoner) : null);
        DamageSource damagesource = getDamageSource(this.damageSources());
        if (level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        boolean flag = entity.hurt(damagesource, f);
        if (flag) {
            if (getSummoner() instanceof LivingEntity summoner) {
                var handler = SpellUtil.getFamiliarHandler(summoner);
                handler.awardBond(handler.getSelectedFamiliar(), calculateHurtXP(f));
            }
            if (entity instanceof LivingEntity livingEntity) {
                var effectManager = SpellUtil.getSpellEffects(livingEntity);
                BuildUpEffect effect = effectManager.getEffectFromDamageType(damagesource.typeHolder().getKey());

                if (effect != null)
                    effectManager.incrementBuildEffects(effect, f);
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

    @Override
    public int getStartTick() {
        return 0;
    }

    @Override
    public int getEndTick() {
        return 0;
    }
}
