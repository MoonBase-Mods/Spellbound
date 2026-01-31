package com.ombremoon.spellbound.common.world.entity.living.familiars;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.entity.behavior.attack.FrogTongueAttack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class FrogEntity extends SBFamiliarEntity {
    private static final EntityDataAccessor<OptionalInt> DATA_TONGUE_TARGET_ID = SynchedEntityData.defineId(FrogEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);;
    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState croakAnimationState = new AnimationState();
    public final AnimationState tongueAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    public void eraseTongueTarget() {
        this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    public Optional<Entity> getTongueTarget() {
        IntStream var10000 = ((OptionalInt)this.entityData.get(DATA_TONGUE_TARGET_ID)).stream();
        Level var10001 = this.level();
        Objects.requireNonNull(var10001);
        return var10000.mapToObj(var10001::getEntity).filter(Objects::nonNull).findFirst();
    }

    public void setTongueTarget(Entity tongueTarget) {
        this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.of(tongueTarget.getId()));
    }

    public FrogEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.lookControl = new FrogLookControl(this);
        this.setPathfindingMalus(PathType.WATER, 4.0F);
        this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.3F, 0.2F, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SBLivingEntity.createBossAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 1)
                .add(Attributes.JUMP_STRENGTH, 3.7)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.ARMOR, 1.0)
                .add(Attributes.FOLLOW_RANGE, 100.0)
                .add(SBAttributes.MAGIC_RESIST, 0.5);
    }

    @Override
    public Vec3 getRidingOffset() {
        return new Vec3(0, 0, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            if (getTongueTarget().isPresent()) this.setPose(Pose.USING_TONGUE);
            else this.setPose(Pose.STANDING);
            this.swimIdleAnimationState.animateWhen(this.isInWaterOrBubble() && !this.walkAnimation.isMoving(), this.tickCount);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_POSE.equals(key)) {
            Pose pose = this.getPose();
            if (pose == Pose.LONG_JUMPING) {
                this.jumpAnimationState.start(this.tickCount);
            } else {
                this.jumpAnimationState.stop();
            }

            if (pose == Pose.CROAKING) {
                this.croakAnimationState.start(this.tickCount);
            } else {
                this.croakAnimationState.stop();
            }

            if (pose == Pose.USING_TONGUE) {
                this.tongueAnimationState.start(this.tickCount);
            } else {
                this.tongueAnimationState.stop();
            }
        }

        super.onSyncedDataUpdated(key);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.FROG_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return super.calculateFallDamage(fallDistance, damageMultiplier) - 5;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    protected void updateWalkAnimation(float partialTick) {
        float f;
        if (this.jumpAnimationState.isStarted()) {
            f = 0.0F;
        } else {
            f = Math.min(partialTick * 25.0F, 1.0F);
        }

        this.walkAnimation.update(f, 0.4F);
    }


    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return this.distanceTo(entity) < 2f;
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget(),
                new SequentialBehaviour<>(
                        new SetWalkTargetToAttackTarget<>(),
                        new FrogTongueAttack()
                                .startCondition(entity -> entity.isWithinMeleeAttackRange(BrainUtils.getTargetOfEntity(entity)))
                )
        );
    }

    public int getHeadRotSpeed() {
        return 35;
    }

    public int getMaxHeadYRot() {
        return 5;
    }


    public class FrogLookControl extends LookControl {
        public FrogLookControl(Mob mob) {
            super(mob);
        }

        protected boolean resetXRotOnTick() {
            return FrogEntity.this.getTongueTarget().isEmpty();
        }
    }
}
