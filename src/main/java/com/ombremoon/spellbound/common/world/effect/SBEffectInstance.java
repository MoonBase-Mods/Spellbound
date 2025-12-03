package com.ombremoon.spellbound.common.world.effect;

import com.ombremoon.spellbound.networking.PayloadHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SBEffectInstance extends MobEffectInstance {
    private final LivingEntity causeEntity;
    private final boolean willGlow;

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect) {
        this(causeEntity, effect, 0);
    }

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect, int duration) {
        this(causeEntity, effect, duration, false);
    }

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect, int duration, boolean willGlow) {
        this(causeEntity, effect, duration, willGlow, 0);
    }

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect, int duration, boolean willGlow, int amplifier) {
        this(causeEntity, effect, duration, willGlow, amplifier, false, true);
    }

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect, int duration, boolean willGlow, int amplifier, boolean ambient, boolean visible) {
        this(causeEntity, effect, duration, willGlow, amplifier, ambient, visible, visible);
    }

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect, int duration, boolean willGlow, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        this(causeEntity, effect, duration, willGlow, amplifier, ambient, visible, showIcon, null);
    }

    public SBEffectInstance(LivingEntity causeEntity, Holder<MobEffect> effect, int duration, boolean willGlow, int amplifier, boolean ambient, boolean visible, boolean showIcon, @Nullable MobEffectInstance hiddenEffect) {
        super(effect, duration, amplifier, ambient, visible, showIcon, hiddenEffect);
        this.causeEntity = causeEntity;
        this.willGlow = willGlow;
    }

    @Override
    public Set<EffectCure> getCures() {
        return Set.of();
    }

    public LivingEntity getCauseEntity() {
        return this.causeEntity;
    }

    public boolean willGlow() {
        return this.willGlow;
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity) {
        super.onEffectAdded(livingEntity);
        if (!livingEntity.level().isClientSide) {
            if (this.causeEntity instanceof Player player && !this.causeEntity.is(livingEntity) && this.willGlow) {
                PayloadHandler.updateGlowEffect(player, livingEntity.getId(), false);
            }
        }
    }
}
