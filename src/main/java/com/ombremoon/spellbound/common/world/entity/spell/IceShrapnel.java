package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import com.ombremoon.spellbound.common.world.spell.ruin.ice.ShatteringCrystalSpell;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class IceShrapnel extends SpellProjectile<ShatteringCrystalSpell> {
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(IceShrapnel.class, EntityDataSerializers.INT);

    public IceShrapnel(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, 0);
    }

    public int getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(int size) {
        this.entityData.set(SIZE, size);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::shrapnelController));
    }

    protected <S extends GeoAnimatable> PlayState shrapnelController(AnimationState<S> data) {
        int size = this.getSize();
        if (size == 0) {
            data.setAnimation(RawAnimation.begin().thenPlay("small"));
        } else if (size == 1) {
            data.setAnimation(RawAnimation.begin().thenPlay("medium"));
        } else {
            data.setAnimation(RawAnimation.begin().thenLoop("large"));
        }
        return PlayState.CONTINUE;
    }
}
