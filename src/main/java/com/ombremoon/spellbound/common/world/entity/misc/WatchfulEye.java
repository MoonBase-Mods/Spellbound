package com.ombremoon.spellbound.common.world.entity.misc;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.entity.ai.target.ExtendedTargetOrRetaliate;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.object.ExtendedTargetingConditions;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.registry.SBLSensors;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import net.tslat.smartbrainlib.util.SensoryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class WatchfulEye extends SBLivingEntity {
    public WatchfulEye(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createWatchfulEyeAttributes() {
        return SBLivingEntity.createBossAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    public int getStartTick() {
        return 0;
    }

    @Override
    public int getEndTick() {
        return 0;
    }

    @Override
    protected void applyGravity() {

    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return ObjectArrayList.of(
                new WatchfulEyeSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtAttackTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new ExtendedTargetOrRetaliate<>()
                        .useMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER)
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new WatchfulEyeAttackTarget(10)
        );
    }

    static class WatchfulEyeAttackTarget extends DelayedBehaviour<WatchfulEye> {
        private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.ATTACK_TARGET).noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);

        @Nullable
        protected LivingEntity target = null;

        public WatchfulEyeAttackTarget(int delayTicks) {
            super(delayTicks);
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, WatchfulEye entity) {
            this.target = BrainUtils.getTargetOfEntity(entity);

            return this.target != null && !this.target.isInvisible() && !this.target.hasEffect(SBEffects.MAGI_INVISIBILITY)/* && entity.getSensing().hasLineOfSight(this.entity)*/;
        }

        @Override
        protected void start(WatchfulEye entity) {
            entity.swing(InteractionHand.MAIN_HAND);
            BehaviorUtils.lookAtEntity(entity, this.target);
        }

        @Override
        protected void stop(WatchfulEye entity) {
            this.target = null;
        }

        @Override
        protected void doDelayedAction(WatchfulEye entity) {
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, 10);

            if (this.target == null)
                return;

            if (this.target.isInvisible() || this.target.hasEffect(SBEffects.MAGI_INVISIBILITY))
                return;

            entity.doHurtTarget(this.target);
        }
    }

    public static class WatchfulEyeSensor<E extends LivingEntity> extends PredicateSensor<Player, E> {
        private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);

        @Nullable
        protected SquareRadius radius = null;

        public WatchfulEyeSensor() {
            super((player, entity) -> !player.isSpectator());
        }

        /**
         * Set the radius for the sensor to scan.
         *
         * @param radius The coordinate radius, in blocks
         * @return this
         */
        public WatchfulEyeSensor<E> setRadius(double radius) {
            return setRadius(radius, radius);
        }

        /**
         * Set the radius for the sensor to scan.
         *
         * @param xz The X/Z coordinate radius, in blocks
         * @param y  The Y coordinate radius, in blocks
         * @return this
         */
        public WatchfulEyeSensor<E> setRadius(double xz, double y) {
            this.radius = new SquareRadius(xz, y);

            return this;
        }

        @Override
        public List<MemoryModuleType<?>> memoriesUsed() {
            return MEMORIES;
        }

        @Override
        public SensorType<? extends ExtendedSensor<?>> type() {
            return SBLSensors.NEARBY_PLAYERS.get();
        }

        @Override
        protected void doTick(ServerLevel level, E entity) {
            SquareRadius radius = this.radius;

            if (radius == null) {
                double dist = entity.getAttributeValue(Attributes.FOLLOW_RANGE);

                radius = new SquareRadius(dist, dist);
            }

            List<Player> players = EntityRetrievalUtil.getPlayers(entity, radius.xzRadius(), radius.yRadius(), radius.xzRadius(), player -> predicate().test(player, entity));

            players.sort(Comparator.comparingDouble(entity::distanceToSqr));

            List<Player> targetablePlayers = new ObjectArrayList<>(players);

            targetablePlayers.removeIf(pl -> !isEntityTargetableIgnoreLineOfSight(entity, pl));

            List<Player> attackablePlayers = new ObjectArrayList<>(targetablePlayers);

            attackablePlayers.removeIf(pl -> !SensoryUtils.isEntityAttackableIgnoringLineOfSight(entity, pl));

            BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_PLAYERS, players);
            BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_PLAYER, targetablePlayers.isEmpty() ? null : targetablePlayers.getFirst());
            BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, attackablePlayers.isEmpty() ? null : attackablePlayers.getFirst());
        }

        private static boolean isEntityTargetableIgnoreLineOfSight(LivingEntity attacker, LivingEntity target) {
            final ExtendedTargetingConditions predicate = BrainUtils.getTargetOfEntity(attacker) == target ?
                    ExtendedTargetingConditions.forLookTarget().withFollowRange().ignoreLineOfSight().skipInvisibilityCheck() :
                    ExtendedTargetingConditions.forLookTarget().withFollowRange().ignoreLineOfSight();

            return predicate.test(attacker, target);
        }
    }
}
