package com.ombremoon.spellbound.common.world.effect;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SBEffect extends MobEffect {
    private List<SpellModifier> spellModifiers;

    public SBEffect(MobEffectCategory category, int color) {
        super(category, color);
        spellModifiers = new ArrayList<>();
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        super.onEffectAdded(livingEntity, amplifier);
        if (!livingEntity.level().isClientSide) {
            if (this == SBEffects.SILENCED.value()) {
                RenderUtil.triggerEntityEffect(livingEntity, EffectData.Entity.of(CommonClass.customLocation("purge_magic_dominant"), livingEntity.getId(), EntityEffectExecutor.AutoRotate.NONE)
                        .setOffset(0, -0.3, 0)
                        .setDelay(5));
            }
        }
    }

    public void onEffectRemoved(LivingEntity livingEntity, int amplifier) {
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public SBEffect addSpellModifiers(SpellModifier modifier) {
        this.spellModifiers.add(modifier);
        return this;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
    }

    public List<SpellModifier> getSpellModifiers() {
        return this.spellModifiers;
    }
}
