package com.ombremoon.spellbound.common.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SBEffect extends MobEffect {
    public SBEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void onEffectRemoved(LivingEntity livingEntity, int amplifier) {
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
