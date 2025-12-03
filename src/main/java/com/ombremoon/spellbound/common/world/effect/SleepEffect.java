package com.ombremoon.spellbound.common.world.effect;

import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SleepEffect extends SBEffect {
    public SleepEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        var handler = SpellUtil.getSpellHandler(livingEntity);
        handler.consumeMana((float) (handler.getMaxMana() * 0.1F + 30.0F), true);
    }
}
