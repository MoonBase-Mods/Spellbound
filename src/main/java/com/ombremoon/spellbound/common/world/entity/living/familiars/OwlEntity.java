package com.ombremoon.spellbound.common.world.entity.living.familiars;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.entity.projectile.SeekingMagicProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class OwlEntity extends SBFamiliarEntity implements RangedAttackMob {
    public static final String TWISTED_HEAD = "twisted_head";

    public OwlEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.navigation = new FlyingPathNavigation(this, level);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, MOVEMENT, 5, data -> {
            if (!data.getAnimatable().isIdle()) {
                data.setAnimation(RawAnimation.begin().thenPlay("wing flap"));
            } else return PlayState.STOP;
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, TWISTED_HEAD, 0, state -> PlayState.CONTINUE)
                .triggerableAnim("twist", RawAnimation.begin().thenPlay("head turns")));
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SBLivingEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget(),
                new SequentialBehaviour<>(
                        new SetWalkTargetToAttackTarget<>(),
                        new AnimatableRangedAttack<>(20)
                                .attackInterval((entity) -> 20)
                )
        );
    }

    @Override
    public void performRangedAttack(LivingEntity target, float v) {
        SeekingMagicProjectile projectile = new SeekingMagicProjectile(level(), target, getSummoner() instanceof LivingEntity summoner ? summoner : null);
        projectile.setPos(this.getEyePosition());
        this.level().addFreshEntity(projectile);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ALLAY_HURT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SBLivingEntity.createBossAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(Attributes.JUMP_STRENGTH, 3.7)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.ARMOR, 1.0)
                .add(Attributes.FOLLOW_RANGE, 100.0)
                .add(SBAttributes.MAGIC_RESIST, 0.5);
    }
}
