package com.ombremoon.spellbound.common.events.custom;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingDodgeEvent extends LivingEvent implements ICancellableEvent {
    private final DamageContainer container;

    public LivingDodgeEvent(LivingEntity entity, DamageContainer container) {
        super(entity);
        this.container = container;
    }

    public DamageContainer getContainer() {
        return this.container;
    }

    public DamageSource getSource() {
        return this.container.getSource();
    }

    public float getAmount() {
        return this.container.getNewDamage();
    }
}
