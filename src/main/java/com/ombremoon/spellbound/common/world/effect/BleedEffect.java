package com.ombremoon.spellbound.common.world.effect;

import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedEffect extends SBEffect {
    public BleedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(SpellUtil.damageSource(livingEntity.level(), SBDamageTypes.BLEED, null, null), livingEntity.getMaxHealth() * 0.15F);
    }

    @Override
    public boolean isInstantenous() {
        return super.isInstantenous();
    }
}
