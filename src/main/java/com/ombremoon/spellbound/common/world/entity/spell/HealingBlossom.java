package com.ombremoon.spellbound.common.world.entity.spell;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.entity.VFXSpellEntity;
import com.ombremoon.spellbound.common.world.spell.divine.HealingBlossomSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
    private static final EntityDataAccessor<Boolean> FULLY_BLOOMED = SynchedEntityData.defineId(HealingBlossom.class, EntityDataSerializers.BOOLEAN);

    public HealingBlossom(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());

        //Bloom completed
        if (!this.isFullyBloomed() && !this.isSlowBlooming() && !this.isFastBlooming()) {
            this.setFullyBloomed(true);
            this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("healing_blossom_cast"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                    .setOffset(0, 0.1, 0));
        }
    }

    public boolean isFullyBloomed() {
        return this.entityData.get(FULLY_BLOOMED);
    }

    public void setFullyBloomed(boolean bloomed) {
        this.entityData.set(FULLY_BLOOMED, bloomed);
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
        builder.define(FULLY_BLOOMED, false);
    }

    public boolean isEmpowered() {
        return this.entityData.get(EMPOWERED);
    }
}
