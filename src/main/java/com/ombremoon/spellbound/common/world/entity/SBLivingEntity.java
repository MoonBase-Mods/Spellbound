package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.client.particle.EffectCache;
import com.ombremoon.spellbound.client.particle.FXEmitter;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.util.EntityUtil;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SBLivingEntity extends PathfinderMob implements SmartBrainOwner<SBLivingEntity>, GeoEntity, FXEmitter, Loggable {
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
        return entity instanceof LivingEntity livingEntity && (this.isOwner(livingEntity) || SpellUtil.IS_ALLIED.test(this.getOwner(), livingEntity)) || super.isAlliedTo(entity);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isBoss() && this.getOwner() != null;
    }

    @Nullable
    public Entity getOwner() {
        return this.level().getEntity(this.entityData.get(OWNER_ID));
    }

    public void setOwner(Entity entity) {
        this.setOwner(entity.getId());
    }

    public void setOwner(int id) {
        this.entityData.set(OWNER_ID, id);
    }

    protected boolean isOwner(LivingEntity entity) {
        Entity owner = this.getOwner();
        return owner != null && owner.is(entity);
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
        Entity entity = this.getOwner();
        if (entity != null)
            compound.putInt("SpellboundOwner", this.getOwner().getId());

        compound.putInt("BossPhase", this.getPhase());
        compound.putBoolean("Spawned", this.hasSpawned());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOwner(compound.getInt("SpellboundOwner"));
        this.setPhase(compound.getInt("BossPhase"));
        this.entityData.set(SPAWNED, compound.getBoolean("Spawned"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Spawn", 0, state -> {
            if (this.isStarting())
                return state.setAndContinue(RawAnimation.begin().thenPlay("spawn"));
            return PlayState.STOP;
        }));
    }

    @Override
    public void onClientRemoval() {
        this.handleFXRemoval();
    }

    public boolean isStarting() {
        return this.tickCount <= getStartTick();
    }

    public abstract int getStartTick();

    public boolean isEnding() {
        return this.deathTime < this.getEndTick();
    }

    public abstract int getEndTick();

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
}
