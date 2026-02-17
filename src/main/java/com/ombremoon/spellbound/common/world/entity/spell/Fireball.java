package com.ombremoon.spellbound.common.world.entity.spell;

import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import com.ombremoon.spellbound.common.world.entity.VFXSpellProjectile;
import com.ombremoon.spellbound.common.world.spell.ruin.fire.FireballSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Fireball extends VFXSpellProjectile<FireballSpell> {
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STICK_TARGET = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STICKY = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.BOOLEAN);
    private int stickTime = 0;

    public Fireball(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected EffectBuilder<? extends FXEffectExecutor> getEffect() {
        return null;
    }

    @Override
    protected ResourceLocation getEffectLocation() {
        return CommonClass.customLocation("fireball");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, 0);
        builder.define(STICKY, false);
        builder.define(STICK_TARGET, -1);
    }

    @Override
    public void tick() {
        if (this.isSticky()) {
            Entity target = this.level().getEntity(this.entityData.get(STICK_TARGET));
            FireballSpell spell = this.getSpell();
            if (target instanceof LivingEntity) {
                this.setPos(target.getX(), target.getY(), target.getZ());
                this.setDeltaMovement(target.getDeltaMovement());

                if (!this.level().isClientSide && this.tickCount >= this.stickTime) {
                    spell.explode(this);
                }

                return;
            } else if (this.isColliding(this.blockPosition(), this.level().getBlockState(this.blockPosition()))) {
                this.setDeltaMovement(Vec3.ZERO);
                this.setPos(this.blockPosition().getX() + .5, this.blockPosition().getY() + .5, this.blockPosition().getZ() + .5);

                if (!this.level().isClientSide && this.tickCount >= this.stickTime) {
                    spell.explode(this);
                }

                return;
            }
        }

        this.refreshDimensions();
        super.tick();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable((this.getSize() * 0.5F) + 0.5F, (this.getSize() * 0.5F) + 0.5F);
    }

    public int getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(int size) {
        this.entityData.set(SIZE, size);
    }

    public boolean isSticky() {
        return this.entityData.get(STICKY);
    }

    public void setSticky(boolean sticky) {
        this.entityData.set(STICKY, sticky);
    }

    public void setStickTarget(Entity entity) {
        this.entityData.set(STICK_TARGET, entity.getId());
    }

    public void setStickTime(int time) {
        this.stickTime = time;
    }
}
