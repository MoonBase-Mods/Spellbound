package com.ombremoon.spellbound.common.world.entity.misc;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.world.entity.VFXEntity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class ShadowMist extends VFXEntity {
    public static final ResourceLocation VFX = CommonClass.customLocation("shadow_mist");
    public ShadowMist(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected EffectBuilder<?> getEffect() {
        return EffectBuilder.Entity.of(VFX, this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                .setOffset(0, -1.5, 0);
    }

    @Override
    protected ResourceLocation getEffectLocation() {
        return VFX;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        this.checkInsideBlocks();
        this.handlePortal();
        if (this.level() instanceof ServerLevel) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
            for (LivingEntity entity : list) {
                if (entity.isAlive() && !entity.isSpectator() && !this.isPassengerOfSameVehicle(entity)) {
                    entity.addEffect(new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, 50));
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200));
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }
}
