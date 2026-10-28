package com.ombremoon.spellbound.common.world.entity.projectile;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.effects.SacredBladeEffect;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.world.entity.VFXSpellProjectile;
import com.ombremoon.spellbound.common.world.spell.divine.SmiteSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class SacredBlade extends VFXSpellProjectile<SmiteSpell> {
    public static final ResourceLocation LIGHT_VFX = CommonClass.customLocation("smite_projectile");
    public static final ResourceLocation DARK_VFX = CommonClass.customLocation("smite_projectile_dark");
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(SacredBlade.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DARK = SynchedEntityData.defineId(SacredBlade.class, EntityDataSerializers.BOOLEAN);
    private int life;

    public SacredBlade(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected EffectBuilder<?> getEffect() {
        return SacredBladeEffect.SacredBladeBuilder.of(this.getId());
        /*EffectBuilder.Entity.of(this.getEffectLocation(), this.getId(), EntityEffectExecutor.AutoRotate.LOOK)
                .setOffset(0.5, -0.25, 0)
                .setRotation(180, 180, 0);*/
    }

    @Override
    protected ResourceLocation getEffectLocation() {
        return this.isDark() ? DARK_VFX : LIGHT_VFX;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, 1);
        builder.define(DARK, false);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", (short)this.life);
        compound.putShort("size", (short)this.getSize());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.life = compound.getShort("life");
        this.setSize(compound.getShort("size"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            SmiteSpell smiteSpell = this.getOrCreateSpell();
            if (smiteSpell != null) {
                List<LivingEntity> targets = smiteSpell.getAttackableEntities(this, 0);
                for (LivingEntity target : targets) {
                    if (target != this.getSummoner()) {
                        this.onHitEntity(new EntityHitResult(target));
                    }
                }
            }
        }

        this.tickDespawn();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity livingentity) {
            SmiteSpell smiteSpell = this.getOrCreateSpell();
            if (smiteSpell != null) {
                if (smiteSpell.isChoice(SBSkills.BLACK_BLADE) && smiteSpell.hurt(livingentity, 3F)) {
                    smiteSpell.addBlackBladeDebuff(livingentity);
                } else if (livingentity.getType().is(EntityTypeTags.UNDEAD)) {
                    smiteSpell.hurt(livingentity, 3F);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return EntityDimensions.scalable(dimensions.width() * this.getSize() * 0.75F, dimensions.height());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SIZE.equals(key)) {
            this.refreshDimensions();
        }
    }

    protected void tickDespawn() {
        this.life++;
        if (this.life >= 100) {
            this.discard();
        }
    }

    public int getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(int size) {
        this.entityData.set(SIZE, size);
    }

    public boolean isDark() {
        return this.entityData.get(DARK);
    }

    public void setDark(boolean isDark) {
        this.entityData.set(DARK, isDark);
    }
}
