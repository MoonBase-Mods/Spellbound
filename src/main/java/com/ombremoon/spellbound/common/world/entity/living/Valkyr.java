package com.ombremoon.spellbound.common.world.entity.living;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.world.entity.ai.sensor.NearbyShrineSensor;
import com.ombremoon.spellbound.common.world.entity.ai.target.ExtendedInvalidateAttackTarget;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomFlyingTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import java.util.List;
import java.util.UUID;

public class Valkyr extends SBLivingEntity implements NeutralMob {
    private static final EntityDataAccessor<Boolean> IN_FLIGHT = SynchedEntityData.defineId(Valkyr.class, EntityDataSerializers.BOOLEAN);
    private final PathNavigation groundNav;
    private final PathNavigation flightNav;
    private final MoveControl groundControl;
    private final MoveControl flightControl;
    private Vec3 moveTarget = Vec3.ZERO;

    public Valkyr(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.groundNav = new SmoothGroundNavigation(this, level);
        this.flightNav = new FlyingPathNavigation(this, level);
        this.groundControl = new MoveControl(this);
        this.flightControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_FLIGHT, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("InFlight", this.isInFlight());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlight(compound.getBoolean("InFlight"));
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int remainingPersistentAngerTime) {

    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID persistentAngerTarget) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (isInFlight())
            return;

        super.checkFallDamage(y, onGround, state, pos);
    }

    public boolean isNearShrine() {
        var memory = SBMemoryTypes.NEARBY_SHRINES.get();
        return BrainUtils.hasMemory(this, memory) && !BrainUtils.getMemory(this, memory).isEmpty();
    }

    @Nullable
    public BlockPos getNearestShrine() {
        var blockList = BrainUtils.getMemory(this, SBMemoryTypes.NEARBY_SHRINES.get());
        if (blockList != null) {
            var blocks = blockList.getFirst();
            return blocks.getFirst();
        }
        return null;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.level().isClientSide) {
//            this.setFlight(true);
//            log("Flying: " + isInFlight());
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public @NotNull PathNavigation getNavigation() {
        return isInFlight() ? this.flightNav : this.groundNav;
    }

    @Override
    public @NotNull MoveControl getMoveControl() {
        return isInFlight() ? this.flightControl : this.moveControl;
    }

    public boolean isInFlight() {
        return this.entityData.get(IN_FLIGHT);
    }

    public void setFlight(boolean flying) {
        this.entityData.set(IN_FLIGHT, flying);
        this.navigation = flying ? this.flightNav : this.groundNav;
        this.moveControl = flying ? this.flightControl : this.groundControl;
    }

    public static AttributeSupplier.Builder createValkyrAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.45)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<Valkyr>()
                        .setRadius(10)
                        .setPredicate((player, valkyr) -> {
                            var effects = SpellUtil.getSpellEffects(player);
                            return !player.getAbilities().invulnerable && effects.getJudgement() < 100;
                        }),
                new HurtBySensor<>(),
                new NearbyShrineSensor<SBLivingEntity>().setRadius(15),
                new NearbyBlocksSensor<SBLivingEntity>().setRadius(15)
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<Valkyr>(),
                new CircleAroundShrine(),
                new WalkOrRunToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>()
                                .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                                .attackablePredicate(target -> target.isAlive() && !(target instanceof Valkyr) && (!(target instanceof Player player) || !player.getAbilities().invulnerable) && !isAlliedTo(target)),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>())/*,
                new OneRandomBehaviour<>(
                        new SetWalkTargetToBlock<>()
                                .predicate((pathfinderMob, blockPosBlockStatePair) -> !blockPosBlockStatePair.getSecond().is(SBTags.Blocks.DIVINE_SHRINE)),
                        new SetRandomWalkTarget<Valkyr>()
                                .avoidWaterWhen(valkyr -> true)
                                .startCondition(valkyr -> !isNearShrine() && !isInFlight()),
                        new Idle<Valkyr>()
                                .runFor(valkyr -> valkyr.getRandom().nextInt(30, 60))

                )*/
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new ExtendedInvalidateAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new OneRandomBehaviour<>(
                                new SetRandomFlyingTarget<Valkyr>()
                                        .verticalWeight(valkyr -> 2)
                                        .flightTargetPredicate((valkyr, vec3) -> {
                                            if (!isNearShrine())
                                                return false;

                                            if (BrainUtils.getTargetOfEntity(valkyr) == null)
                                                return false;

                                            BlockPos shrinePos = getNearestShrine();
                                            boolean flag = Vec3.atBottomCenterOf(shrinePos).distanceToSqr(vec3) <= 100;

                                            Vec3 targetVec = BrainUtils.getTargetOfEntity(valkyr).position();
                                            double vertDist = vec3.y - targetVec.y;
                                            boolean flag1 = vertDist >= 5.0F && vertDist <= 7.0F;

                                            BlockPos targetPos = new BlockPos(Mth.floor(vec3.x), Mth.floor(vec3.y), Mth.floor(vec3.z));
                                            BlockState state = valkyr.level().getBlockState(targetPos);
                                            return flag && flag1 && state.isAir();
                                        })
                                        .setRadius(20)
                                        .speedModifier(1.5F),
                                new Idle<Valkyr>()
                                        .runFor(valkyr -> 20/*valkyr.getRandom().nextInt(100, 160)*/)
                        ).startCondition(Valkyr::isInFlight),
                        new SetWalkTargetToAttackTarget<>()
                )
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, MOVEMENT, 5, this::valkyrMotionController));
    }

    @Override
    public int getStartTick() {
        return 0;
    }

    @Override
    public int getEndTick() {
        return 0;
    }

    protected <T extends GeoAnimatable> PlayState valkyrMotionController(AnimationState<T> data) {
        if (this.isInFlight()) {
            data.setAnimation(RawAnimation.begin().thenLoop("idle_fly"));
        } else {
            if (data.isMoving()) {
                if (this.getSharedFlag(3)) {
                    data.setAnimation(RawAnimation.begin().thenLoop("run"));
                } else {
                    data.setAnimation(RawAnimation.begin().thenLoop("walk"));
                }
            } else if (isDeadOrDying()) {
                data.setAnimation(RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME));
            } else {
                data.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
        }
        return PlayState.CONTINUE;
    }

    public class CircleAroundShrine extends ExtendedBehaviour<Valkyr> {
        public static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
                .hasMemory(SBLMemoryTypes.NEARBY_BLOCKS.get())
                .hasMemory(SBMemoryTypes.NEARBY_SHRINES.get())
                .noMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                .noMemory(MemoryModuleType.ATTACK_TARGET);

        private float angle;

        public CircleAroundShrine() {
            this.noTimeout();
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected boolean canStillUse(ServerLevel level, Valkyr valkyr, long gameTime) {
            return isNearShrine() && BrainUtils.getTargetOfEntity(valkyr) == null;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, Valkyr valkyr) {
            return isNearShrine() && BrainUtils.getTargetOfEntity(valkyr) == null;
        }

        @Override
        protected void start(Valkyr valkyr) {
            this.selectNext();
        }

        @Override
        protected void tick(Valkyr valkyr) {
            if (this.touchingTarget()) {
                this.selectNext();
            }
        }
        protected boolean touchingTarget() {
            WalkTarget target = BrainUtils.getMemory(Valkyr.this, MemoryModuleType.WALK_TARGET);
            return target == null || target.getTarget().currentPosition().distanceToSqr(Valkyr.this.getX(), Valkyr.this.getY(), Valkyr.this.getZ()) < 4.0;
        }

        private void selectNext() {
            BlockPos shrine = Valkyr.this.getNearestShrine();
            this.angle = this.angle + 15.0F * (float) (Math.PI / 180.0);
            Vec3 vec3 = Vec3.atLowerCornerOf(shrine)
                    .add((9 * Mth.cos(this.angle)), 0, (9 * Mth.sin(this.angle)));
            BrainUtils.setMemory(Valkyr.this, MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, 1.0F, 4));
        }
    }
}
