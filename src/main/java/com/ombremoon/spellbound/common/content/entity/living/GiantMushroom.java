package com.ombremoon.spellbound.common.content.entity.living;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.content.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.content.entity.SmartSpellEntity;
import com.ombremoon.spellbound.common.content.spell.summon.WildMushroomSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class GiantMushroom extends SmartSpellEntity<WildMushroomSpell> {
    protected GiantMushroom(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return List.of();
    }

    static class MushroomLookAtTarget extends ExtendedBehaviour<GiantMushroom> {
        private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.LOOK_TARGET);

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, GiantMushroom entity) {
            return testAndInvalidateLookTarget(entity);
        }

        @Override
        protected boolean shouldKeepRunning(GiantMushroom entity) {
            return testAndInvalidateLookTarget(entity);
        }

        @Override
        protected void tick(GiantMushroom entity) {
            BrainUtils.withMemory(entity, MemoryModuleType.LOOK_TARGET, target -> {
                Vec3 pos = target.currentPosition();
                if (pos.distanceToSqr(entity.position()) < 100.0) {
                    double d1 = pos.x() - entity.getX();
                    double d2 = pos.z() - entity.getZ();
                    entity.setYRot((float) (-((float)Mth.atan2(d1, d2)) * (180.0F / Math.PI)));
                    entity.yBodyRot = entity.getYRot();
                }
            });
        }

        protected boolean testAndInvalidateLookTarget(GiantMushroom entity) {
            PositionTracker lookTarget = BrainUtils.getMemory(entity, MemoryModuleType.LOOK_TARGET);

            if (lookTarget == null)
                return false;

            if (lookTarget instanceof EntityTracker entityTracker && !entityTracker.getEntity().isAlive()) {
                BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET);

                return false;
            }

            return true;
        }
    }

    public static class BounceToTarget extends ExtendedBehaviour<GiantMushroom> {
        private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.ATTACK_TARGET).noMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS);
        private static final List<Integer> ALLOWED_ANGLES = Lists.newArrayList(65, 70, 75, 80);

        @Nullable
        protected LivingEntity target = null;
        private Function<GiantMushroom, UniformInt> timeBetweenBounces = giantMushroom -> UniformInt.of(40, 100);
        protected Function<GiantMushroom, Float> maxJumpVelocityMultiplier = giantMushroom -> 3.6F;
        @Nullable
        protected Vec3 chosenJump;
        protected int findJumpTries;
        protected long prepareJumpStart;
        protected Optional<Vec3> initialPosition;
        private  BiPredicate<GiantMushroom, BlockPos> acceptableLandingSpot = (giantMushroom, blockPos) -> true;

        public BounceToTarget() {
            runFor(entity -> 200);
            cooldownFor(entity -> entity.getRandom().nextInt(40));
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        public BounceToTarget bounceInterval(Function<GiantMushroom, UniformInt> bounceInterval) {
            this.timeBetweenBounces = bounceInterval;

            return this;
        }

        public BounceToTarget jumpVelocityMultiplier(Function<GiantMushroom, Float> jumpVelocityMultiplier) {
            this.maxJumpVelocityMultiplier = jumpVelocityMultiplier;

            return this;
        }

        public BounceToTarget acceptableLandingSpot(BiPredicate<GiantMushroom, BlockPos> predicate) {
            this.acceptableLandingSpot = predicate;

            return this;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, GiantMushroom entity) {
            boolean flag = entity.onGround()
                    && !entity.isInWater()
                    && !entity.isInLava()
                    && !level.getBlockState(entity.blockPosition()).is(Blocks.HONEY_BLOCK);
            if (!flag) {
                entity.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenBounces.apply(entity).sample(level.random) / 2);
            }

            this.target = BrainUtils.getTargetOfEntity(entity);

            return flag && entity.getSensing().hasLineOfSight(this.target) && entity.distanceToSqr(this.target) >= 25;
        }

        @Override
        protected boolean canStillUse(ServerLevel level, GiantMushroom entity, long gameTime) {
            boolean flag = this.initialPosition.isPresent()
                    && this.initialPosition.get().equals(entity.position())
                    && this.findJumpTries > 0
                    && !entity.isInWaterOrBubble()
                    && this.chosenJump != null;
            if (!flag && entity.getBrain().getMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS).isEmpty()) {
                entity.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenBounces.apply(entity).sample(level.random) / 2);
                entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
            }

            return flag;
        }

        @Override
        protected void start(ServerLevel level, GiantMushroom entity, long gameTime) {
            super.start(level, entity, gameTime);
            this.chosenJump = null;
            this.initialPosition = Optional.of(entity.position());
        }

        @Override
        protected void tick(ServerLevel level, GiantMushroom entity, long gameTime) {
            super.tick(level, entity, gameTime);
            if (this.chosenJump != null) {
                if (gameTime - this.prepareJumpStart >= 40L) {
                    entity.setYRot(entity.yBodyRot);
                    entity.setDiscardFriction(true);
                    double d0 = this.chosenJump.length();
                    double d1 = d0 + entity.getJumpBoostPower();
                    entity.setDeltaMovement(this.chosenJump.scale(d1 / d0));
                } else {
                    this.pickCandidate(level, entity, gameTime);
                }
            }
        }

        protected void pickCandidate(ServerLevel level, GiantMushroom entity, long prepareJumpStart) {
            if (this.target != null) {
                BlockPos blockpos = this.target.blockPosition();
                if (this.isAcceptableLandingPosition(level, entity, blockpos)) {
                    Vec3 vec3 = Vec3.atCenterOf(blockpos);
                    Vec3 vec31 = this.calculateOptimalJumpVector(entity, vec3);
                    if (vec31 != null) {
                        entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(blockpos));
                        this.chosenJump = vec31;
                        this.prepareJumpStart = prepareJumpStart;
                    }
                }
            }
        }

        private boolean isAcceptableLandingPosition(ServerLevel level, GiantMushroom entity, BlockPos pos) {
            BlockPos blockpos = entity.blockPosition();
            int i = blockpos.getX();
            int j = blockpos.getZ();
            return (i != pos.getX() || j != pos.getZ()) && this.acceptableLandingSpot.test(entity, pos);
        }

        @Nullable
        protected Vec3 calculateOptimalJumpVector(GiantMushroom entity, Vec3 target) {
            List<Integer> list = Lists.newArrayList(ALLOWED_ANGLES);
            Collections.shuffle(list);
            float f = (float)(entity.getAttributeValue(Attributes.JUMP_STRENGTH) * (double)this.maxJumpVelocityMultiplier.apply(entity));

            for (int i : list) {
                Optional<Vec3> optional = LongJumpUtil.calculateJumpVectorForAngle(entity, target, f, i, true);
                if (optional.isPresent()) {
                    return optional.get();
                }
            }

            return null;
        }
    }
}
