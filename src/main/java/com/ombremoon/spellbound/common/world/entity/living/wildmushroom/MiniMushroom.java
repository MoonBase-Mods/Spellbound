package com.ombremoon.spellbound.common.world.entity.living.wildmushroom;

import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.entity.ai.attack.MushroomExplosion;
import com.ombremoon.spellbound.common.world.entity.ai.move.FollowSummoner;
import com.ombremoon.spellbound.common.world.entity.ai.sensor.HurtOwnerSensor;
import com.ombremoon.spellbound.common.world.entity.ai.sensor.OwnerAttackSenor;
import com.ombremoon.spellbound.common.world.entity.ai.target.ExtendedTargetOrRetaliate;
import com.ombremoon.spellbound.common.world.spell.summon.WildMushroomSpell;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;

import java.util.List;

public class MiniMushroom extends LivingMushroom {
    private static final EntityDataAccessor<Boolean> ENLARGED = SynchedEntityData.defineId(MiniMushroom.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DAZED = SynchedEntityData.defineId(MiniMushroom.class, EntityDataSerializers.BOOLEAN);

    public MiniMushroom(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public MiniMushroom(Level level, GiantMushroom owner) {
        super(SBEntities.MINI_MUSHROOM.get(), level);
        this.setOwner(owner);
    }

    public static AttributeSupplier.Builder createMiniMushroomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.JUMP_STRENGTH, 0.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ENLARGED, false);
        builder.define(DAZED, false);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!itemStack.is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        } else {
            if (!this.isSpellCast() && this.getOwner() instanceof GiantMushroom mushroom) {
                if (!this.isDazed()) {
                    return InteractionResult.PASS;
                }


                this.setDazed(false);
                this.enlarge();
                this.setOwner(player);
                itemStack.consume(1, player);
                BrainUtils.setTargetOfEntity(this, mushroom);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                var skills = SpellUtil.getSkills(player);
                if (!skills.hasSkill(SBSkills.PROLIFERATION)) {
                    return InteractionResult.PASS;
                } else {
                    BlockPos blockPos = this.blockPosition();
                    if (!this.level().isClientSide) {
                        if (RandomUtil.percentChance(0.15F)) {
                            WildMushroomSpell spell = this.getSpell();
                            GiantMushroom giantMushroom = spell.summonEntity(spell.getContext(), SBEntities.GIANT_MUSHROOM.get(), this.position());
                            spell.setGiantMushroom(giantMushroom.getId());
                            this.discard();
                        }
                    }

                    ParticleUtils.spawnParticles(this.level(), blockPos, 15, 1.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
                    itemStack.consume(1, player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return this.isDazed() && source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || super.hurt(source, amount);
    }

    @Override
    protected void tickDeath() {
        if (!this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && !this.isRemoved()) {
            if (!this.isSpellCast()
                    && this.getOwner() instanceof GiantMushroom mushroom
                    && mushroom.getPhase() == 2
                    && AbstractSpell.isSpellDamage(damageSource)) {
                this.setDazed(true);
                this.setHealth(4.0F);
                return;
            }
        }

        super.die(damageSource);
        if (this.level().isClientSide) {
            Vec3 position = this.position();
            this.addFX(
                    EffectBuilder.Block.of(CommonClass.customLocation("mushroom_explosion"), BlockPos.containing(position.x, position.y, position.z))
                            .setOffset(0, 0.08, 0)
            );
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isExploding()) {
            if (this.explosionTimer-- == 1) {
                Vec3 position = this.position();
                this.addFX(
                        EffectBuilder.Block.of(CommonClass.customLocation("mushroom_explosion"), BlockPos.containing(position.x, position.y, position.z))
                                .setOffset(0, 0.08, 0)
                );
            }
        }
    }

    @Override
    public List<? extends ExtendedSensor<? extends SBLivingEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<MiniMushroom>()
                        .setRadius(32)
                        .setPredicate((player, mushroom) -> !mushroom.isAlliedTo(player)),
                new HurtOwnerSensor<MiniMushroom>()
                        .setPredicate((source, miniMushroom) -> !(source.getEntity() != null && miniMushroom.isAlliedTo(source.getEntity()))),
                new OwnerAttackSenor<>(),
                new HurtBySensor<MiniMushroom>()
                        .setPredicate((source, miniMushroom) -> !(source.getEntity() != null && miniMushroom.isAlliedTo(source.getEntity())))
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<MiniMushroom>()
                        .stopIf(MiniMushroom::isDazed),
                new FollowSummoner<>(),
                new WalkOrRunToWalkTarget<MiniMushroom>()
                        .startCondition(miniMushroom -> !miniMushroom.isDazed())
                        .stopIf(miniMushroom -> miniMushroom.isExploding() || miniMushroom.isDazed())
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new ExtendedTargetOrRetaliate<>()
                                .noTargetSwapping()
                                .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<MiniMushroom>(),
                        new Idle<MiniMushroom>()
                                .runFor(mushroom -> mushroom.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<MiniMushroom>()
                        .invalidateIf((miniMushroom, livingEntity) -> miniMushroom.isDazed()),
                new SetWalkTargetToAttackTarget<>()
                        .speedMod((miniMushroom, livingEntity) -> 1.5F),
                new MushroomExplosion<MiniMushroom>(30)
                        .explosionRadius(miniMushroom -> {
                            if (miniMushroom.getOwner() instanceof LivingEntity livingEntity) {
                                var skills = SpellUtil.getSkills(livingEntity);
                                if (skills.hasSkill(SBSkills.VILE_INFLUENCE)) {
                                    return 5.0F;
                                }
                            }

                            return 3.0F;
                        })
                        .explosionDamage(miniMushroom -> {
                            if (miniMushroom.getOwner() instanceof GiantMushroom mushroom) {
                                return 8.0F * mushroom.getPhase();
                            }

                            return 12.0F;
                        })
                        .shouldDiscard()
                        .stopIf(MiniMushroom::isDazed)
        );
    }

    public boolean isEnlarged() {
        return this.entityData.get(ENLARGED);
    }

    public void enlarge() {
        this.entityData.set(ENLARGED, true);
    }

    public boolean isDazed() {
        return this.entityData.get(DAZED);
    }

    public void setDazed(boolean dazed) {
        this.entityData.set(DAZED, dazed);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, MOVEMENT, 7, this::miniMushroomController));
        controllers.add(new AnimationController<>(this, EXPLOSION, 0, state -> PlayState.STOP)
                .triggerableAnim("explode", RawAnimation.begin().thenPlay("explode")));
    }

    @Override
    public int getStartTick() {
        return 0;
    }

    @Override
    public int getEndTick() {
        return 0;
    }

    protected <T extends GeoAnimatable> PlayState miniMushroomController(AnimationState<T> data) {
        if (data.isMoving()) {
            if (this.getSharedFlag(3)) {
                data.setAnimation(RawAnimation.begin().thenPlay("run"));
            } else {
                data.setAnimation(RawAnimation.begin().thenPlay("walk"));
            }
        } else {
            data.setAnimation(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.CONTINUE;
    }
}
