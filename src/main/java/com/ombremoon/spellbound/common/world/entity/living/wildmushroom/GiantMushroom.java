package com.ombremoon.spellbound.common.world.entity.living.wildmushroom;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.entity.behavior.attack.MushroomExplosion;
import com.ombremoon.spellbound.common.world.entity.behavior.sensor.HurtOwnerSensor;
import com.ombremoon.spellbound.common.world.entity.behavior.sensor.OwnerAttackSenor;
import com.ombremoon.spellbound.common.world.entity.behavior.target.ExtendedTargetOrRetaliate;
import com.ombremoon.spellbound.common.world.entity.projectile.MushroomProjectile;
import com.ombremoon.spellbound.common.world.entity.spell.WildMushroom;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

//TODO: Make partial recast spells overlap in gui when active OR put a number

public class GiantMushroom extends LivingMushroom implements RangedAttackMob {
    protected static final String ATTACK = "attack";
    protected static final String BOUNCE = "bounce";
    private static final EntityDataAccessor<BlockPos> START_POS = SynchedEntityData.defineId(GiantMushroom.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> INVINCIBLE = SynchedEntityData.defineId(GiantMushroom.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BOUNCING = SynchedEntityData.defineId(GiantMushroom.class, EntityDataSerializers.BOOLEAN);
    private boolean aboutToBounce;
    private float jumpMultiplier;
    private boolean backAtOrigin;
    private final ServerBossEvent bossEvent;

    public GiantMushroom(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        this.explosionTimer = 24;
        this.setStartTick(50);
    }

    public static AttributeSupplier.Builder createGiantMushroomAttributes() {
        return SBLivingEntity.createBossAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.JUMP_STRENGTH, 3.7)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 100.0)
                .add(SBAttributes.MAGIC_RESIST, 0.5)
                .add(SBAttributes.FIRE_SPELL_RESIST, -0.3);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(START_POS, BlockPos.ZERO);
        builder.define(INVINCIBLE, false);
        builder.define(BOUNCING, false);
    }

    public boolean isBouncing() {
        return this.isBouncingAndAirborne() || this.aboutToBounce;
    }

    private void makeInvulnerable() {
        this.setInvincible(true);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.level();
        if (!level.isClientSide) {
            if (!this.onGround()) {
                this.jumpMultiplier = Mth.clamp(this.fallDistance * 0.35F, 3.0F, 22.0F);
                if (this.isBouncing() && this.getY() - this.yo < -0.2000000059604645F) {
                    triggerAnim(BOUNCE, "fall");
                }
            } else if (this.isBouncingAndAirborne() && this.onGround()) {
                float bounceRadius = 4.0F + 20.0F * this.bounceMultiplier();
                List<LivingEntity> collidingEntities = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), livingEntity -> !livingEntity.is(this) && !this.isAlliedTo(livingEntity));
                List<LivingEntity> surroundingEntities = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(bounceRadius), livingEntity ->
                        !livingEntity.is(this)
                        && !this.isAlliedTo(livingEntity)
                        && !collidingEntities.contains(livingEntity)
                        && livingEntity.onGround());
                collidingEntities.forEach(livingEntity -> this.hurtAndKnockback(livingEntity, 26.0F + 34.0F * this.bounceMultiplier()));
                surroundingEntities.forEach(livingEntity -> this.hurtAndKnockback(livingEntity, this.bounceDamage(livingEntity, 22.0F, bounceRadius)));
                this.setBouncing(false);
                this.jumpMultiplier = 0.0F;

                triggerAnim(BOUNCE, "land");
                if (!this.isAttacking())
                    BrainUtils.setForgettableMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN, true, 20);
            }
        } else {
            if (this.isExploding()) {
                if (this.explosionTimer-- == 1) {
                    Vec3 position = this.position();
                    this.addFX(
                            EffectBuilder.Block.of(CommonClass.customLocation("boss_mushroom_explosion"), BlockPos.containing(position.x, position.y, position.z))
                    );
                    this.explosionTimer = 24;
                    this.setExploding(false);
                }
            }
        }
        this.setYBodyRot(this.getYHeadRot());
    }

    private void hurtAndKnockback(LivingEntity target, float hurtAmount) {
        if (this.hurtTarget(target, hurtAmount)) {
            target.knockback(2.5, this.getX() - target.getX(), this.getZ() - target.getZ());
            target.hurtMarked = true;
        }
    }

    private float bounceDamage(LivingEntity target, float minDamage, float bounceRadius) {
        return (minDamage + 34.0F * this.bounceMultiplier()) * Mth.clamp(1.0F - this.distanceTo(target) / bounceRadius, 0.5F, 1.0F);
    }

    private float bounceMultiplier() {
        return (this.jumpMultiplier - 3.0F) / 19.0F;
    }

    public void setBouncing(boolean bouncing) {
        this.entityData.set(BOUNCING, bouncing);
    }

    public boolean isBouncingAndAirborne() {
        return this.entityData.get(BOUNCING);
    }

    public void setStartPos(BlockPos startPos) {
        this.entityData.set(START_POS, startPos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(START_POS);
    }

    public boolean isInvincible() {
        return this.entityData.get(INVINCIBLE);
    }

    public void setInvincible(boolean invincible) {
        this.entityData.set(INVINCIBLE, invincible);
    }

    @Override
    protected SpellMastery getBossLevel() {
        return SpellMastery.ADEPT;
    }

    @Override
    protected int getMaxBossPhases() {
        return 3;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getBossProgress());
    }

    @Override
    public void setCustomName(@org.jetbrains.annotations.Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (!this.hasSummoner())
            this.bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (!this.hasSummoner())
            this.bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean flag = this.getPhase() == 2;
            Entity entity = source.getDirectEntity();
            if (entity instanceof MiniMushroom mushroom) {
                if (flag && mushroom.getSummoner() != this) {
                    amount *= 1.5F;
                    return super.hurt(source, amount);
                } else {
                    return false;
                }
            } else if (flag && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            }

            return super.hurt(source, amount);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (super.isInvulnerableTo(source)) {
            return true;
        } else {
            return source.is(Tags.DamageTypes.IS_POISON);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (onGround) {
            this.resetFallDistance();
        } else if (y < 0.0) {
            this.fallDistance -= (float)y;
        }
    }

    @Override
    public int getHeadRotSpeed() {
        return 3;
    }

    @Override
    public @org.jetbrains.annotations.Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
        this.setStartPos(this.blockPosition());
        if (this.hasSummoner()) {
            AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
            if (health != null)
                health.setBaseValue(60);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("StartPos", NbtUtils.writeBlockPos(this.getStartPos()));
        compound.putBoolean("BackAtOrigin", this.backAtOrigin);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        NbtUtils.readBlockPos(compound, "StartPos").ifPresent(this::setStartPos);
        this.backAtOrigin = compound.getBoolean("BackAtOrigin");
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        MushroomProjectile mushroom = new MushroomProjectile(this.level(), this);
        mushroom.setPrimaryProjectile(true);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - mushroom.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2) * 0.20000000298023224;
        mushroom.shoot(d0, d1 + d3, d2, 1.6F, 10.0F);
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        this.level().addFreshEntity(mushroom);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<GiantMushroom>()
                        .setPredicate((player, giantMushroom) -> !giantMushroom.isAlliedTo(player)),
                new HurtOwnerSensor<GiantMushroom>()
                        .setPredicate((source, giantMushroom) -> !(source.getEntity() != null && giantMushroom.isAlliedTo(source.getEntity()))),
                new OwnerAttackSenor<GiantMushroom>(),
                new HurtBySensor<GiantMushroom>()
                        .setPredicate((source, giantMushroom) -> !(source.getEntity() != null && giantMushroom.isAlliedTo(source.getEntity())))
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new LookAtAttackTarget<>(),
                new BounceToTarget()
                        .startCondition(giantMushroom -> !giantMushroom.isAttacking() && !giantMushroom.wasSummoned() && giantMushroom.getPhase() != 2),
                new BounceToTarget()
                        .jumpToBlock(GiantMushroom::getStartPos)
                        .startCondition(giantMushroom -> giantMushroom.getPhase() == 2 && !giantMushroom.backAtOrigin)
                        .whenStopping(giantMushroom -> giantMushroom.backAtOrigin = true)
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new ExtendedTargetOrRetaliate<>()
                        .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)

        );
    }

    //TODO:
    // - Root Rise
    // - Mega Explosion
    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<GiantMushroom>() {
                    @Override
                    protected boolean isTiredOfPathing(GiantMushroom entity) {
                        return false;
                    }
                },
                new OneRandomBehaviour<>(
                        new OneRandomBehaviour<>(
                                newMushroomBounceAttack(
                                        new BounceToTarget()
                                                .jumpVector(giantMushroom -> new Vec3(0, 4.0, 0)),
                                        giantMushroom -> {
                                            LivingEntity target = BrainUtils.getTargetOfEntity(giantMushroom);
                                            return target != null && giantMushroom.distanceToSqr(target) < 144;
                                        },
                                        80,
                                        600
                                ),
                                newMushroomBounceAttack(
                                        new MultiBounce()
                                                .numBounce(giantMushroom -> UniformInt.of(2, 3))
                                                .jumpTime(giantMushroom -> 20)
                                                .jumpVector(giantMushroom -> new Vec3(0, 0.9, 0)),
                                        giantMushroom -> {
                                            LivingEntity target = BrainUtils.getTargetOfEntity(giantMushroom);
                                            return target != null && giantMushroom.distanceToSqr(target) < 25;
                                        },
                                        40,
                                        80
                                ),
                                newMushroomAttack(
                                        new SurroundTargetWithMushrooms(30),
                                        50,
                                        200
                                )
                        )
                                .startCondition(giantMushroom -> !giantMushroom.hasSummoner() && giantMushroom.getPhase() == 3),
                        newMushroomBounceAttack(
                                new MultiBounce()
                                        .numBounce(giantMushroom -> giantMushroom.getPhase() > 2 ? UniformInt.of(3, 5) : UniformInt.of(3, 8))
                                        .pickCandidate()
                                        .jumpScaleMultiplier(giantMushroom -> 1.0F)
                                        .jumpTime(giantMushroom -> 20),
                                40,
                                600),
                        newMushroomAttack(
                                new AnimatableRangedAttack<GiantMushroom>(28) {
                                    @Override
                                    protected void start(GiantMushroom entity) {
                                        super.start(entity);
                                        entity.triggerAnim(ATTACK, "shroom_toss");
                                    }
                                }
                                        .attackInterval(giantMushroom -> 37)
                                        .attackRadius(64),
                                giantMushroom -> {
                                    LivingEntity target = BrainUtils.getTargetOfEntity(giantMushroom);
                                    return target != null && giantMushroom.isFacingTarget(target) && giantMushroom.distanceToSqr(target) > 36;
                                },
                                28,
                                giantMushroom -> this.getPhase() == 2 ? 300 : 60),
                        newMushroomAttack(
                                new MushroomExplosion<GiantMushroom>(25)
                                        .attackRadius(giantMushroom -> 7.0F)
                                        .explosionRadius(giantMushroom -> 4.0F)
                                        .explosionDamage(giantMushroom -> 16.0F),
                               41,
                                60)
                )
        );
    }

    private ExtendedBehaviour<GiantMushroom> newMushroomAttack(ExtendedBehaviour<GiantMushroom> behaviour, int attackTicks, int cooldownTicks) {
        return newMushroomAttack(behaviour, giantMushroom -> true, attackTicks, giantMushroom -> cooldownTicks);
    }

    private ExtendedBehaviour<GiantMushroom> newMushroomAttack(ExtendedBehaviour<GiantMushroom> behaviour, int attackTicks, Function<GiantMushroom, Integer> cooldownTicks) {
        return newMushroomAttack(behaviour, giantMushroom -> true, attackTicks, cooldownTicks);
    }

    private ExtendedBehaviour<GiantMushroom> newMushroomAttack(ExtendedBehaviour<GiantMushroom> behaviour, Predicate<GiantMushroom> additionalCondition, int attackTicks, Function<GiantMushroom, Integer> cooldownTicks) {
        return behaviour
                .stopIf(GiantMushroom::isBouncing)
                .startCondition(giantMushroom -> this.hasSpawned() && !giantMushroom.isBouncing() && !giantMushroom.isAttacking() && additionalCondition.test(giantMushroom))
                .whenStarting(giantMushroom -> giantMushroom.startAttack(attackTicks))
                .cooldownFor(cooldownTicks);
    }

    private ExtendedBehaviour<GiantMushroom> newMushroomBounceAttack(ExtendedBehaviour<GiantMushroom> behaviour, int attackTicks, int cooldownTicks) {
        return newMushroomBounceAttack(behaviour, giantMushroom -> true, attackTicks, cooldownTicks);
    }

    private ExtendedBehaviour<GiantMushroom> newMushroomBounceAttack(ExtendedBehaviour<GiantMushroom> behaviour, Predicate<GiantMushroom> additionalCondition, int attackTicks, int cooldownTicks) {
        return behaviour
                .stopIf(giantMushroom -> giantMushroom.getPhase() == 2)
                .startCondition(giantMushroom -> this.hasSpawned() && !giantMushroom.wasSummoned() && !giantMushroom.isBouncing() && !giantMushroom.isAttacking() && giantMushroom.getPhase() != 2 && additionalCondition.test(giantMushroom))
                .whenStarting(giantMushroom -> giantMushroom.startAttack(attackTicks))
                .cooldownFor(giantMushroom -> cooldownTicks);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, MOVEMENT, 7, this::giantMushroomController));
        controllers.add(new AnimationController<>(this, EXPLOSION, 0, state -> PlayState.STOP)
                .triggerableAnim("explode", RawAnimation.begin().thenPlay("explode")));
        controllers.add(new AnimationController<>(this, BOUNCE, 2, state -> PlayState.STOP)
                .triggerableAnim("jump", RawAnimation.begin().thenPlayAndHold("jump_start"))
                .triggerableAnim("fall", RawAnimation.begin().thenPlay("jump_fall_transition").thenLoop("falling_idle"))
                .triggerableAnim("land", RawAnimation.begin().thenPlay("jump_land")));
        controllers.add(new AnimationController<>(this, ATTACK, 0, state -> PlayState.STOP)
                .triggerableAnim("shroom_toss", RawAnimation.begin().thenPlay("shroom_toss"))
                .triggerableAnim("raise", RawAnimation.begin().thenPlay("root_raise")));
    }

    protected <T extends GeoAnimatable> PlayState giantMushroomController(AnimationState<T> data) {
        if (!this.isBouncing()) {
            if (this.isStarting() && !this.hasSpawned()) {
                data.setAnimation(RawAnimation.begin().thenPlay("spawn"));
            } else if (this.isDeadOrDying()) {
                data.setAnimation(RawAnimation.begin().thenPlay("death"));
            } else {
                data.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
        }

        return PlayState.CONTINUE;
    }

    static class BounceToTarget extends ExtendedBehaviour<GiantMushroom> {
        private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);

        @Nullable
        protected LivingEntity target = null;
        @Nullable
        protected Vec3 chosenJump;
        protected long prepareJumpStart;
        protected Optional<Vec3> initialPosition;
        protected Function<GiantMushroom, BlockPos> targetBlock = giantMushroom -> null;
        protected Function<GiantMushroom, Vec3> jumpVector = giantMushroom -> null;
        protected Function<GiantMushroom, Float> jumpStrength = giantMushroom -> (float) giantMushroom.getAttributeValue(Attributes.JUMP_STRENGTH);
        protected Function<GiantMushroom, Integer> jumpTime = giantMushroom -> 40;
        protected Function<GiantMushroom, Float> gravityMultiplier = giantMushroom -> 0.28F;

        public BounceToTarget() {
            runFor(entity -> 200);
            cooldownFor(entity -> entity.random.nextIntBetweenInclusive(50, 100));
        }

        public BounceToTarget jumpToBlock(Function<GiantMushroom, BlockPos> blockTarget) {
            this.targetBlock = blockTarget;

            return this;
        }

        public BounceToTarget jumpVector(Function<GiantMushroom, Vec3> jumpVector) {
            this.jumpVector = jumpVector;

            return this;
        }

        public BounceToTarget jumpStrength(Function<GiantMushroom, Float> jumpStrength) {
            this.jumpStrength = jumpStrength;

            return this;
        }

        public BounceToTarget jumpTime(Function<GiantMushroom, Integer> jumpTime) {
            this.jumpTime = jumpTime;

            return this;
        }

        public BounceToTarget gravityMultiplier(Function<GiantMushroom, Float> gravityMultiplier) {
            this.gravityMultiplier = gravityMultiplier;

            return this;
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, GiantMushroom entity) {
            boolean flag = entity.onGround()
                    && !entity.isInWater()
                    && !entity.isInLava()
                    && !level.getBlockState(entity.blockPosition()).is(Blocks.HONEY_BLOCK);
            this.target = BrainUtils.getTargetOfEntity(entity);

            return flag && this.target != null && entity.getSensing().hasLineOfSight(this.target) && !entity.isAttacking() && !entity.isBouncingAndAirborne();
        }

        @Override
        protected boolean canStillUse(ServerLevel level, GiantMushroom entity, long gameTime) {
            return this.initialPosition.isPresent()
                    && this.initialPosition.get().equals(entity.position())
                    && !entity.isInWaterOrBubble()
                    && this.chosenJump != null
                    && !entity.isBouncingAndAirborne();
        }

        @Override
        protected void start(ServerLevel level, GiantMushroom entity, long gameTime) {
            super.start(level, entity, gameTime);
            this.chosenJump = null;
            this.initialPosition = Optional.of(entity.position());
            this.pickCandidate(entity, gameTime);
            entity.aboutToBounce = true;
        }

        @Override
        protected void tick(ServerLevel level, GiantMushroom entity, long gameTime) {
            super.tick(level, entity, gameTime);
            if (this.chosenJump != null) {
                if (gameTime - this.prepareJumpStart >= this.jumpTime.apply(entity) && entity.onGround()) {
                    entity.setDiscardFriction(true);
                    entity.setDeltaMovement(this.chosenJump);
                    entity.setBouncing(true);
                    entity.aboutToBounce = false;
                } else {
                    this.pickCandidate(entity, gameTime);
                }

                if (gameTime - this.prepareJumpStart == 23) {
                    entity.triggerAnim(BOUNCE, "jump");
                }
            }
        }

        @Override
        protected void stop(GiantMushroom entity) {
            super.stop(entity);
            entity.setDiscardFriction(false);
            entity.aboutToBounce = false;
        }

        protected void pickCandidate(GiantMushroom entity, long prepareJumpStart) {
            if (this.chosenJump == null) {
                this.prepareJumpStart = prepareJumpStart;
            }

            Vec3 jumpVec = this.jumpVector.apply(entity);
            if (jumpVec != null) {
                this.chosenJump = jumpVec;
            } else if (this.target != null) {
                BlockPos blockPos = this.target.blockPosition();
                BlockPos blockPos1 = this.targetBlock.apply(entity);
                if (blockPos1 != null)
                    blockPos = blockPos1;

                if (this.isAcceptableLandingPosition(entity, blockPos)) {
                    Vec3 vec31 = this.calculateJumpVector(entity, blockPos, this.jumpStrength.apply(entity));
                    if (vec31 != null) {
                        this.chosenJump = vec31;
                    }
                }
            }
        }

        private Vec3 calculateJumpVector(GiantMushroom entity, BlockPos target, float jumpPower) {
            double gravity = this.gravityMultiplier.apply(entity);
            Vec3 startPos = entity.position();
            Vec3 endPos = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
            double dx = endPos.x - startPos.x;
            double dy = endPos.y - startPos.y;
            double dz = endPos.z - startPos.z;
            double velocityY = Math.sqrt(2 * gravity * jumpPower);
            double d0 = 0.5 * gravity;
            double time = (velocityY + Math.sqrt(-velocityY * -velocityY + 2 * d0 * dy)) / (2 * d0);
            if (time <= 0) {
                return Vec3.ZERO;
            }

            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            double horizontalVelocity = horizontalDistance / time;
            double velocityX = (dx / horizontalDistance) * horizontalVelocity;
            double velocityZ = (dz / horizontalDistance) * horizontalVelocity;
            return new Vec3(velocityX, velocityY, velocityZ);
        }

        private boolean isAcceptableLandingPosition(GiantMushroom entity, BlockPos pos) {
            BlockPos blockpos = entity.blockPosition();
            int i = blockpos.getX();
            int j = blockpos.getZ();
            return (i != pos.getX() || j != pos.getZ()) && this.defaultAcceptableLandingSpot(entity, pos);
        }

        private boolean defaultAcceptableLandingSpot(GiantMushroom mob, BlockPos pos) {
            Level level = mob.level();
            BlockPos blockpos = pos.below();
            return level.getBlockState(blockpos).isSolidRender(level, blockpos) && mob.getPathfindingMalus(WalkNodeEvaluator.getPathTypeStatic(mob, pos)) == 0.0F;
        }
    }

    static class MultiBounce extends BounceToTarget {
        private Function<GiantMushroom, UniformInt> numBounces = giantMushroom -> UniformInt.of(1, 1);
        private Function<GiantMushroom, Float> jumpMultiplier = giantMushroom -> 1.5F;
        private Function<GiantMushroom, Integer> attackIntervalSupplier = giantMushroom -> 60;
        private boolean pickCandidate = false;
        private Vec3 firstJumpVec;
        private int maxBounces;
        private int bounceCount;

        public MultiBounce() {
            noTimeout();
        }

        public MultiBounce numBounce(Function<GiantMushroom, UniformInt> numBounces) {
            this.numBounces = numBounces;

            return this;
        }

        public MultiBounce jumpScaleMultiplier(Function<GiantMushroom, Float> jumpMultiplier) {
            this.jumpMultiplier = jumpMultiplier;

            return this;
        }

        public MultiBounce pickCandidate() {
            this.pickCandidate = true;

            return this;
        }

        public MultiBounce attackInterval(Function<GiantMushroom, Integer> attackInterval) {
            this.attackIntervalSupplier = attackInterval;

            return this;
        }

        @Override
        protected boolean canStillUse(ServerLevel level, GiantMushroom entity, long gameTime) {
            return !entity.isInWaterOrBubble()
                    && this.target != null
                    && this.bounceCount < this.maxBounces;
        }

        @Override
        protected void start(ServerLevel level, GiantMushroom entity, long gameTime) {
            super.start(level, entity, gameTime);
            this.maxBounces = this.numBounces.apply(entity).sample(level.random);
            this.firstJumpVec = this.jumpVector.apply(entity);
        }

        @Override
        protected void tick(ServerLevel level, GiantMushroom entity, long gameTime) {
            if (!entity.isBouncingAndAirborne()) {
                super.tick(level, entity, gameTime);
                if (gameTime - this.prepareJumpStart == 3) {
                    entity.triggerAnim(BOUNCE, "jump");
                }
            } else {
                if (entity.shouldDiscardFriction()) {
                    entity.setDiscardFriction(false);
                    BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
                    this.target = BrainUtils.getTargetOfEntity(entity);
                    this.bounceCount++;
                    Vec3 jumpVec = this.jumpVector.apply(entity);
                    if (jumpVec != null) {
                        this.jumpVector = giantMushroom -> jumpVec.scale(this.jumpMultiplier.apply(entity));
                    }
                }

                this.prepareJumpStart = gameTime;
                if (this.pickCandidate) {
                    this.pickCandidate(entity, gameTime);
                }
            }
        }

        @Override
        protected void stop(GiantMushroom entity) {
            super.stop(entity);
            this.maxBounces = 0;
            this.bounceCount = 0;
            this.jumpVector = giantMushroom -> this.firstJumpVec;
        }
    }

    static class SurroundTargetWithMushrooms extends DelayedBehaviour<GiantMushroom> {
        private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.ATTACK_TARGET).noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);
        private Function<GiantMushroom, UniformInt> numMushroomsSupplier = giantMushroom -> UniformInt.of(4, 6);
        private int maxMushrooms;

        @Nullable
        protected LivingEntity target = null;

        public SurroundTargetWithMushrooms(int delayTicks) {
            super(delayTicks);
        }

        public SurroundTargetWithMushrooms numMushrooms(Function<GiantMushroom, UniformInt> numMushrooms) {
            this.numMushroomsSupplier = numMushrooms;

            return this;
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, GiantMushroom entity) {
            this.target = BrainUtils.getTargetOfEntity(entity);

            return entity.getSensing().hasLineOfSight(this.target);
        }

        @Override
        protected void start(GiantMushroom entity) {
            super.start(entity);
            this.maxMushrooms = this.numMushroomsSupplier.apply(entity).sample(entity.random);
            entity.triggerAnim(ATTACK, "raise");
        }

        @Override
        protected void doDelayedAction(GiantMushroom entity) {
            if (this.target == null)
                return;

            Level level = entity.level();
            BlockPos blockPos = this.target.blockPosition();
            double d0 = Math.min(this.target.getY(), entity.getY());
            double d1 = Math.max(this.target.getY(), entity.getY()) + 1.0;
            float f = (float)Mth.atan2(this.target.getZ() - entity.getZ(), this.target.getX() - entity.getX());
            for (int i = 0; i < this.maxMushrooms; i++) {
                float f2 = f + (float)i * (float) Math.PI * 2.0F / this.maxMushrooms + (float) (Math.PI * 2.0 / 5.0);
                this.createMushroom(level, entity, blockPos.getX() + (double)Mth.cos(f2) * 2.5, blockPos.getZ() + (double)Mth.sin(f2) * 2.5, d0, d1);
            }
        }

        @Override
        protected void stop(GiantMushroom entity) {
            super.stop(entity);
            this.maxMushrooms = 0;
        }

        protected void createMushroom(Level level, GiantMushroom owner, double x, double z, double minY, double maxY) {
            BlockPos blockpos = BlockPos.containing(x, maxY, z);
            boolean flag = false;
            double d0 = 0.0;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(level, blockpos1, Direction.UP)) {
                    if (!level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(minY) - 1);

            if (flag) {
                WildMushroom mushroom = new WildMushroom(level, owner);
                mushroom.setPos(x, blockpos.getY() + d0, z);
                level.addFreshEntity(mushroom);
            }
        }
    }
}
