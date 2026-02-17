package com.ombremoon.spellbound.common.magic.api.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class DeathEvent extends SpellEvent {
    private final LivingDeathEvent event;
    private final DamageSource source;

    public DeathEvent(LivingEntity caster, LivingDeathEvent event) {
        super(caster, event);
        this.event = event;
        this.source = event.getSource();
    }

    public DamageSource getSource() {
        return this.source;
    }

    public LivingEntity getKilledEntity() {
        return this.event.getEntity();
    }
}
