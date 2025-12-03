/*
package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.spell.ruin.air.CycloneSpell;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.util.SpellUtil;
import com.ombremoon.spellbound.util.math.NoiseGenerator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

import java.util.List;
import java.util.Map;

public class Cyclone extends SpellEntity<CycloneSpell> {
    private static final EntityDataAccessor<Integer> CYCLONE_STACK = SynchedEntityData.defineId(Cyclone.class, EntityDataSerializers.INT);
    protected static final ResourceLocation FROSTFRONT = CommonClass.customLocation("frostfront");
    private final Map<Entity, Integer> catchDuration = new Object2IntOpenHashMap<>();
    private final Map<Entity, Integer> throwCooldown = new Object2IntOpenHashMap<>();
    private final List<Entity> caughtEntities = new ObjectArrayList<>();
    private final List<Entity>thrownEntities = new ObjectArrayList<>();
    private int growthTick;
    private int debrisCounter;
    private int debrisCooldown;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;

    public Cyclone(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Cyclone(Level level, double xPos, double yPos, double zPos) {
        this(SBEntities.CYCLONE.get(), level);
        this.xo = xPos;
        this.yo = yPos;
        this.zo = zPos;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CYCLONE_STACK, 1);
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = steps;
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();
        this.refreshDimensions();

        if (this.level().isClientSide && this.getControllingPassenger() instanceof LocalPlayer localPlayer) {
            this.setInput(localPlayer.input.up, localPlayer.input.down, localPlayer.input.left, localPlayer.input.right);
            localPlayer.handsBusy |= localPlayer.input.left || localPlayer.input.right || localPlayer.input.up || localPlayer.input.down;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.9D, 0.8D));

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.isVehicle()) {
            this.setYRot(this.getControllingPassenger().getYRot());
        }

        if (this.getControllingPassenger() != null) {
            if (this.isControlledByLocalInstance()) {
                if (this.level().isClientSide)
                    this.controlCyclone();
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        } else {
            int seed = this.random.nextInt();
            int time = this.tickCount;
            int intensity = skills != null && skills.hasSkill(SBSkills.GALE_FORCE.value()) ? 2 : 1;
            double d0 = getNoise(seed, intensity, time);
            double d2 = getNoise(seed + 2, intensity, time);
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x + d0, vec3.y, vec3.z + d2);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        this.checkInsideBlocks();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity && this.skills != null && this.handler != null) {
            CycloneSpell spell = this.getSpell();
            if (spell != null) {
                if (spell.isInactive)
                    discard();

                double range = 5 + this.getStacks() * 2.5;
                if (skills.hasSkill(SBSkills.VORTEX.value()))
                    this.checkCycloneCollision(spell);

                if (skills.hasSkill(SBSkills.FALLING_DEBRIS.value())) {
                    if (!this.level().isClientSide)
                        this.createDebris((int) Math.floor(range));
                }

                List<Entity> caughtList = this.level().getEntities(this, this.getBoundingBox(), entity -> {
                    if (entity instanceof LivingEntity livingEntity)
                        return !spell.checkForCounterMagic(livingEntity);
                    return !(entity instanceof SpellEntity);
                });
                List<Entity> pullList = this.level().getEntities(this, this.getBoundingBox().inflate(range), entity -> {
                    if (entity instanceof LivingEntity livingEntity && spell.checkForCounterMagic(livingEntity))
                        return false;
                    return !(entity instanceof SpellEntity);
                });
                if (!pullList.isEmpty()) {
                    for (Entity entity : pullList) {
                        if (!entity.is(owner)) {
                            if (thrownEntities.contains(entity) && this.tickCount >= entity.getData(SBData.THROWN_TICK)) {
                                caughtEntities.remove(entity);
                                catchDuration.remove(entity);
                                thrownEntities.remove(entity);
                                throwCooldown.remove(entity);
                            }

                            float strength = skills.hasSkill(SBSkills.HURRICANE.value()) ? 3.0F : 1.5F;
                            if (skills.hasSkill(SBSkills.WHIRLING_TEMPEST.value())) {
                                if (!thrownEntities.contains(entity)) {
                                    float distance = (float) entity.distanceToSqr(this);
                                    float pullStrength = skills.hasSkill(SBSkills.HURRICANE.value()) ? 0.0375F : 0.02F;
                                    pullStrength = distance <= (this.getBbWidth() * this.getBbWidth() + range * range) / 2 ? pullStrength : (float) (pullStrength * 0.5);
                                    pullStrength *= this.getStacks();
                                    Vec3 direction = this.getBoundingBox().getCenter().subtract(entity.position()).normalize().scale(pullStrength).multiply(1, this.getStacks() > 2 ? 1.5 : (double) 4 / this.getStacks(), 1);
                                    entity.setDeltaMovement(entity.getDeltaMovement().add(direction));
                                }
                            } else {
                                if (entity instanceof LivingEntity livingEntity)
                                    livingEntity.knockback(strength, this.getX() - entity.getX(), this.getZ() - entity.getZ());
                            }
                            entity.hurtMarked = true;
                        }
                    }
                }

                if (!caughtList.isEmpty() && skills.hasSkill(SBSkills.WHIRLING_TEMPEST.value())) {
                    for (Entity entity : caughtList) {
                        if (!this.caughtEntities.contains(entity) && Math.abs(entity.getY() - (this.getY() + this.getBbHeight())) < this.getBbHeight() / 2) {
                            caughtEntities.add(entity);
                            catchDuration.put(entity, this.tickCount + 40 + this.random.nextInt(40));
                        }

                        if (caughtEntities.contains(entity)) {
                            int duration = catchDuration.get(entity);
                            if (this.tickCount >= duration) {
                                caughtEntities.remove(entity);
                                catchDuration.remove(entity);
                                Vec3 vec3 = new Vec3(RandomUtil.randomValueUpTo(1.0), 0.4, RandomUtil.randomValueUpTo(1.0F)).scale(1.0F + this.getStacks() * 0.5F);
                                entity.setDeltaMovement(vec3);
                                entity.hurtMarked = true;
                                thrownEntities.add(entity);
                                entity.setData(SBData.THROWN_TICK, this.tickCount + 60);
                            } else if (!caughtList.contains(entity)) {
                                caughtEntities.remove(entity);
                                catchDuration.remove(entity);
                            }
                        }

                        if (!entity.is(owner)) {
                            float radius = this.getBbWidth() / 3;
                            float height = this.getStacks() > 1 ? this.getBbHeight() / 4 : this.getBbHeight() / 5;
                            if (!thrownEntities.contains(entity) && caughtEntities.contains(entity))
                                entity.setPos(this.getX() + radius * Math.sin(entity.tickCount * 100), this.getBoundingBox().getCenter().y + height * Math.sin(entity.tickCount * 50), this.getZ() + radius * Math.cos(entity.tickCount * 100));

                            if (entity.tickCount % 20 == 0 && entity instanceof LivingEntity livingEntity) {
                                if (skills.hasSkill(SBSkills.FROSTFRONT.value())) {
                                    spell.hurt(livingEntity, SBDamageTypes.RUIN_FROST, 4.0F);
                                    spell.addSkillBuff(
                                            livingEntity,
                                            SBSkills.PURSUIT,
                                            BuffCategory.HARMFUL,
                                            SkillBuff.ATTRIBUTE_MODIFIER,
                                            new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(FROSTFRONT, 0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                            200);
                                }

                                if (skills.hasSkill(SBSkills.STATIC_CHARGE.value()))
                                    spell.hurt(livingEntity, SBDamageTypes.RUIN_SHOCK, 4.0F);
                            }
                        }
                    }
                }
            } else {
                if (!this.level().isClientSide)
                    discard();
            }
        }

        if (this.debrisCooldown >= 0) {
            if (this.debrisCooldown-- == 0) {
                this.debrisCounter = 0;
                this.debrisCooldown = -1;
            }
        }
    }

    private void checkCycloneCollision(CycloneSpell spell) {
        List<Cyclone> cyclones = this.level().getEntitiesOfClass(Cyclone.class, this.getBoundingBox());
        int maxCyclones = skills.hasSkill(SBSkills.MAELSTROM.value()) ? 6 : 3;
        if (!cyclones.isEmpty()) {
            for (Cyclone cyclone1 : cyclones) {
                if (spell != null && cyclone1 != null && !cyclone1.is(this) && (cyclone1.getStacks() < this.getStacks() || this.getId() > cyclone1.getId()) && this.getStacks() < maxCyclones && !this.hasGrown()) {
                    if (cyclone1.getControllingPassenger() instanceof LivingEntity livingEntity)
                        livingEntity.startRiding(this);

                    this.incrementStacks(cyclone1);
                    this.growthTick = 20;
                    spell.setRemainingTicks(spell.getDuration());
                    cyclone1.discard();
                }
            }
        }
        this.growthTick--;
    }

    private void createDebris(int range) {
        if (this.tickCount % 40 == 0) {
            for (int n = 0; n < this.level().random.nextInt(2, 4); n++) {
                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY() - 1);
                int k = Mth.floor(this.getZ());

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + Mth.nextInt(this.random, 0, range) * Mth.nextInt(this.random, -1, 1);
                    int j1 = j + Mth.nextInt(this.random, 0, range);
                    int k1 = k + Mth.nextInt(this.random, 0, range) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    BlockState state = this.level().getBlockState(blockpos);
                    if (createDebris(state, blockpos))
                        break;
                }
            }
        }

        int minX = (int) Math.floor(this.getBoundingBox().minX);
        int maxX = (int) Math.floor(this.getBoundingBox().maxX);
        int minY = (int) Math.floor(this.getBoundingBox().minY);
        int maxY = (int) Math.floor(this.getBoundingBox().maxY);
        int minZ = (int) Math.floor(this.getBoundingBox().minZ);
        int maxZ = (int) Math.floor(this.getBoundingBox().maxZ);
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                for (int k = minZ; k <= maxZ; k++) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockState state = this.level().getBlockState(blockPos);
                    createDebris(state, blockPos);
                }
            }
        }
    }

    private boolean createDebris(BlockState state, BlockPos blockpos) {
        if (this.debrisCounter < 100 && !state.isEmpty() && state.getFluidState().isEmpty() && !state.is(Blocks.OBSIDIAN) && !state.is(Blocks.BEDROCK) && !this.level().isClientSide) {
            FallingBlockEntity entity = FallingBlockEntity.fall(this.level(), blockpos, state);
            entity.setHurtsEntities(1.0F, 20);
            this.debrisCounter++;
            if (this.debrisCounter >= 100)
                this.debrisCooldown = 40;

            return true;
        }
        return false;
    }

    public boolean hasGrown(){
        return this.growthTick > 0;
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            float stepYRot = (float) (this.getYRot() + Mth.wrapDegrees(this.lerpYRot - this.getYRot()) / this.lerpSteps);

            this.setYRot(stepYRot);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    private static double getNoise(int seed, int intensity, int x) {
        NoiseGenerator noiseGenerator = new NoiseGenerator(seed);
        return intensity * 0.5F * noiseGenerator.noise((double) x / 100);
    }

    private void controlCyclone() {
        if (this.isVehicle()) {
            LivingEntity livingEntity = this.getControllingPassenger();
            Vec3 inputVector = this.getInputVector(new Vec3(livingEntity.xxa * 0.8F, 0.0D, livingEntity.zza), this.getSpeed(), this.getYRot());

            if (this.inputLeft || this.inputRight || this.inputUp || this.inputDown) {
                this.setDeltaMovement(this.getDeltaMovement().add(inputVector));
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getStacks());
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        super.positionRider(pPassenger, pCallback);
        if (pPassenger instanceof Player player) {
            player.setYBodyRot(player.getYHeadRot());
        }
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        var holder = SpellUtil.getSkills(pPlayer);
        if (pPlayer.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else {
            if (!this.level().isClientSide && holder.hasSkill(SBSkills.EYE_OF_THE_STORM.value())) {
                return pPlayer.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
        this.fallDistance = 0.0F;
    }


    public float getSpeed() {
        return 0.08F;
    }

    @Override
    public float maxUpStep() {
        return 2.0F;
    }

    public int getStacks(){
        return this.entityData.get(CYCLONE_STACK);
    }

    public void incrementStacks(Cyclone cyclone) {
        this.entityData.set(CYCLONE_STACK, Math.min(getStacks() + cyclone.getStacks(), 6));
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() < this.getMaxPassengers();
    }

    protected int getMaxPassengers() {
        return 1;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        LivingEntity livingEntity1;
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity1 = livingEntity;
        } else {
            livingEntity1 = null;
        }
        return livingEntity1;
    }

    public void setInput(boolean inputUp, boolean inputDown, boolean inputLeft, boolean inputRight) {
        this.inputUp = inputUp;
        this.inputDown = inputDown;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
    }

    @Override
    protected void addPassenger(Entity pPassenger) {
        super.addPassenger(pPassenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYRot, (float) this.lerpXRot);
        }
    }

    public Vec3 getInputVector(Vec3 movementVec, float speedAmount, float angleAmount) {
        double moveLength = movementVec.lengthSqr();
        Vec3 vec3 = (moveLength > 1.0D ? movementVec.normalize() : movementVec).scale(speedAmount);
        float f = Mth.sin(angleAmount * ((float) Math.PI / 180F));
        float f1 = Mth.cos(angleAmount * ((float) Math.PI / 180F));
        return new Vec3(vec3.x * (double) f1 - vec3.z * (double) f, 0, vec3.z * (double) f1 + vec3.x * (double) f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::genericController));
    }
}
*/
