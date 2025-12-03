package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.spell.divine.HealingBlossomSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class HealingBlossom extends SpellEntity<HealingBlossomSpell> {
    private static final EntityDataAccessor<Boolean> FAST_BLOOMING = SynchedEntityData.defineId(HealingBlossom.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EMPOWERED = SynchedEntityData.defineId(HealingBlossom.class, EntityDataSerializers.BOOLEAN);

    public HealingBlossom(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) animateTick(level(), blockPosition(), getRandom());
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public void animateTick(Level p_222504_, BlockPos p_222505_, RandomSource p_222506_) {
        int i = p_222505_.getX();
        int j = p_222505_.getY();
        int k = p_222505_.getZ();
        double d0 = (double)i + p_222506_.nextDouble();
        double d1 = (double)j + 0.7;
        double d2 = (double)k + p_222506_.nextDouble();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = 0; l < 2; l++) {
            blockpos$mutableblockpos.set(i + Mth.nextInt(p_222506_, -5, 5), j - p_222506_.nextInt(5), k + Mth.nextInt(p_222506_, -5, 5));
            BlockState blockstate = p_222504_.getBlockState(blockpos$mutableblockpos);
            if (!blockstate.isCollisionShapeFullBlock(p_222504_, blockpos$mutableblockpos)) {
                p_222504_.addParticle(
                        ParticleTypes.CHERRY_LEAVES,
                        (double)blockpos$mutableblockpos.getX() + p_222506_.nextDouble(),
                        (double)blockpos$mutableblockpos.getY() + p_222506_.nextDouble(),
                        (double)blockpos$mutableblockpos.getZ() + p_222506_.nextDouble(),
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }

    public void teleportToAroundBlockPos(BlockPos pos) {
        for(int i = 0; i < 10; ++i) {
            int j = this.random.nextIntBetweenInclusive(-3, 3);
            int k = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.moveTo((double)x + 0.5, (double)y, (double)z + 0.5, this.getYRot(), this.getXRot());

            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockPos blockpos = pos.subtract(this.blockPosition());
        return this.level().noCollision(this, this.getBoundingBox().move(blockpos));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "bloomController", 0, this::bloomController));
        controllers.add(new AnimationController<>(this, "actionController", 0, this::actionController)
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
        controllers.add(new AnimationController<>(this, "rebirthController", 20, this::rebirthController));
    }

    protected <T extends GeoAnimatable> PlayState bloomController(AnimationState<T> data) {
        if (isFastBlooming()) {
            data.setAnimation(RawAnimation.begin().thenPlay("bloom_fast"));
        } else if (isSlowBlooming()) {
            data.setAnimation(RawAnimation.begin().thenPlay("bloom"));
        } else {
            data.setAnimation(RawAnimation.begin().thenLoop("float_idle"));
        }


        return PlayState.CONTINUE;
    }

    protected <T extends GeoAnimatable> PlayState actionController(AnimationState<T> data) {
        if (!isFastBlooming() && !isSlowBlooming())
            data.setAnimation(RawAnimation.begin().thenLoop("heal_idle"));

        return PlayState.CONTINUE;
    }

    protected <T extends GeoAnimatable> PlayState rebirthController(AnimationState<T> data) {
        if (isEmpowered()) {
            data.setAnimation(RawAnimation.begin().thenPlay("rebirth"));
        } else return PlayState.STOP;

        return PlayState.CONTINUE;
    }

    public boolean isFastBlooming() {
        return this.entityData.get(FAST_BLOOMING) && tickCount < 20;
    }

    public boolean isSlowBlooming() {
        return !this.entityData.get(FAST_BLOOMING) && tickCount < 200;
    }

    public void fastBloom() {
        this.entityData.set(FAST_BLOOMING, true);
    }

    public void setEmpowered(boolean empowered) {
        this.entityData.set(EMPOWERED, empowered);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FAST_BLOOMING, false);
        builder.define(EMPOWERED, false);
    }

    public boolean isEmpowered() {
        return this.entityData.get(EMPOWERED);
    }
}
