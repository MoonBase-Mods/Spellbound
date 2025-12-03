package com.ombremoon.spellbound.common.world.effect;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class TauntEffect extends SBEffect {
    public TauntEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        livingEntity.removeEffect(SBEffects.FEAR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Vec3 source = livingEntity.getData(SBData.MOVEMENT_SOURCE);
        Vec3 direction = new Vec3(source.x - livingEntity.getX(), 0, source.z - livingEntity.getZ()).normalize().scale(0.15);
        if (livingEntity.distanceToSqr(source) <= 4) {
            livingEntity.setDeltaMovement(0, -livingEntity.getGravity(), 0);
        } else {
            livingEntity.setDeltaMovement(direction.add(new Vec3(0, -livingEntity.getGravity(), 0)));
        }

        float yaw = Mth.wrapDegrees((float) (Mth.atan2(direction.z, direction.x) * 180.0F / (float) Math.PI) - 90.0F);
        livingEntity.setYRot(yaw);
        livingEntity.setYHeadRot(yaw);
        livingEntity.setYBodyRot(yaw);
        if (livingEntity instanceof Mob mob) {
            mob.getNavigation().stop();
        } else {
            livingEntity.hurtMarked = true;
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void onEffectRemoved(LivingEntity livingEntity, int amplifier) {
        livingEntity.setData(SBData.MOVEMENT_SOURCE, Vec3.ZERO);
    }

}
