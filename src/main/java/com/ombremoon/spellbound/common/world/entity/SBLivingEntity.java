package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.client.particle.EffectCache;
import com.ombremoon.spellbound.client.particle.FXEmitter;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.util.EntityUtil;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public abstract class SBLivingEntity extends PathfinderMob implements SmartBrainOwner<SBLivingEntity>, GeoEntity, FXEmitter, Loggable, SBSummonable {
    protected static final String MOVEMENT = "Movement";
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(SBLivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BOSS_PHASE = SynchedEntityData.defineId(SBLivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> END_TICK = SynchedEntityData.defineId(SBLivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> START_TICK = SynchedEntityData.defineId(SBLivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SPAWNED = SynchedEntityData.defineId(SBLivingEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final EffectCache effectCache = new EffectCache();

    protected SBLivingEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return createMagicEntityAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public static AttributeSupplier.Builder createMagicEntityAttributes() {
        return Mob.createMobAttributes()
                .add(SBAttributes.MAGIC_RESIST, 0.1)
                .add(SBAttributes.FIRE_SPELL_RESIST)
                .add(SBAttributes.FROST_SPELL_RESIST)
                .add(SBAttributes.SHOCK_SPELL_RESIST);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    public boolean isFacingTarget(Entity target) {
        Vec3 sourceLocation = target.position();
        Vec3 viewVector = this.getViewVector(1.0F);
        viewVector = viewVector.subtract(0.0, viewVector.y, 0.0).normalize();
        Vec3 toSourceLocation = sourceLocation.subtract(this.position()).normalize();
        return toSourceLocation.dot(viewVector) > 0.0;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if ((this.wasSummoned() && !this.hasSummoner()))
                discard();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (!this.hasSpawned() && this.tickCount >= this.getStartTick()) {
                this.spawn();
            }

            if (this.isBoss()) {

            }
        } else {
            this.handleFXRemoval();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SmoothGroundNavigation(this, this.level());
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof LivingEntity livingEntity && (this.isSummoner(livingEntity) || SpellUtil.IS_ALLIED.test(this.getSummoner(), livingEntity)) || super.isAlliedTo(entity);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isBoss() && this.getSummoner() != null;
    }

    @Override
    public Entity getSummoner() {
        return this.level().getEntity(this.entityData.get(OWNER_ID));
    }

    @Override
    public void setSummoner(Entity entity) {
        this.setSummoner(entity.getId());
    }

    @Override
    public void setSummoner(int id) {
        this.entityData.set(OWNER_ID, id);
    }

    public int getPhase() {
        return this.entityData.get(BOSS_PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(BOSS_PHASE, phase);
    }

    protected boolean isBoss() {
        return EntityUtil.isBoss(this);
    }

    protected SpellMastery getBossLevel() {
        return SpellMastery.NOVICE;
    }

    protected int getMaxBossPhases() {
        return 1;
    }

    protected boolean hidesBossPhases() {
        return false;
    }

    protected float getBossProgress() {
        int phase = this.getPhase();
        int maxPhases = this.getMaxBossPhases();
        float progress = this.getPhaseProgress(phase);
        boolean flag = progress <= 0.0F && phase < maxPhases;
        if (flag) {
            this.setPhase(phase + 1);
        }

        if (this.hidesBossPhases()) {
            if (flag) {
                return 1.0F;
            }

            return progress;
        } else {
            return this.getHealth() / this.getMaxHealth();
        }
    }

    protected float getCurrentPhaseThreshold(int phase) {
        return getPhaseThreshold() * (this.getMaxBossPhases() - phase);
    }

    protected float getPhaseThreshold() {
        return this.getMaxHealth() / this.getMaxBossPhases();
    }

    protected float getPhaseProgress(int phase) {
        return (this.getHealth() - this.getCurrentPhaseThreshold(phase)) / this.getPhaseThreshold();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_ID, 0);
        builder.define(BOSS_PHASE, 1);
        builder.define(START_TICK, 0);
        builder.define(END_TICK, 0);
        builder.define(SPAWNED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Entity entity = this.getSummoner();
        if (entity != null)
            compound.putInt("SpellboundOwner", this.getSummoner().getId());

        compound.putInt("BossPhase", this.getPhase());
        compound.putBoolean("Spawned", this.hasSpawned());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSummoner(compound.getInt("SpellboundOwner"));
        this.setPhase(compound.getInt("BossPhase"));
        this.entityData.set(SPAWNED, compound.getBoolean("Spawned"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public void onClientRemoval() {
        this.handleFXRemoval();
    }

    public boolean isStarting() {
        return this.tickCount <= getStartTick();
    }

    public int getStartTick() {
        return this.entityData.get(START_TICK);
    }

    public void setStartTick(int startTick) {
        this.entityData.set(START_TICK, startTick);
    }

    public boolean isEnding() {
        return getEndTick() > 0;
    }

    public int getEndTick() {
        return this.entityData.get(END_TICK);
    }

    public void setEndTick(int endTick) {
        this.entityData.set(END_TICK, this.tickCount + endTick);
    }

    public boolean hasSpawned() {
        return this.entityData.get(SPAWNED);
    }

    public void spawn() {
        this.entityData.set(SPAWNED, true);
    }

    public EntityType<?> entityType() {
        return this.getType();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public EffectCache getFXCache() {
        return this.effectCache;
    }

    protected Predicate<LivingEntity> summonAttackPredicate() {
        if (this.getBrain() == null) return livingEntity -> false; //This is needed, ignore intellij
        Entity target = BrainUtils.getMemory(this, MemoryModuleType.HURT_BY_ENTITY);
        return livingEntity -> !isSummoner(livingEntity) && ((target != null &&  target.is(livingEntity)) || isOwnersTarget(livingEntity));
    }

    protected boolean isOwnersTarget(LivingEntity entity) {
        if (!this.hasData(SBData.TARGET_ID)) return false;
        return entity.getId() == this.getData(SBData.TARGET_ID);
    }

    @Override
    public boolean wasSummoned() {
        return BrainUtils.hasMemory(this, SBMemoryTypes.SUMMON_OWNER.get());
    }

    @Override
    public boolean isSummoner(LivingEntity entity) {
        Entity owner = this.getSummoner();
        return owner != null && owner.is(entity);
    }

    @Override
    public boolean hasSummoner() {
        Entity owner = this.getSummoner();
        return owner != null && owner.isAlive();
    }

    public DamageSource spellDamageSource(Level level) {
        return SpellUtil.damageSource(level, SBDamageTypes.SB_GENERIC, this.getSummoner(), this);
    }
}
