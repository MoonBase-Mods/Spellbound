package com.ombremoon.spellbound.common.magic.api.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class DealtDamageEvent extends SpellEvent {

    public DealtDamageEvent(LivingEntity caster, LivingEvent event) {
        super(caster, event);
    }

    public static class Pre extends DealtDamageEvent {
        private final LivingDamageEvent.Pre event;
        private final DamageContainer container;

        public Pre(LivingEntity caster, LivingDamageEvent.Pre event) {
            super(caster, event);
            this.event = event;
            this.container = this.event.getContainer();
        }

        public LivingEntity getTarget() {
            return this.event.getEntity();
        }

        public DamageContainer getContainer() {
            return this.container;
        }

        public DamageSource getSource() {
            return this.container.getSource();
        }

        public float getNewDamage() {
            return this.container.getNewDamage();
        }

        public float getOriginalDamage() {
            return this.container.getOriginalDamage();
        }

        public void setNewDamage(float newDamage) {
            this.container.setNewDamage(newDamage);
        }
    }

    public static class Post extends DealtDamageEvent {
        private final LivingDamageEvent.Post event;

        public Post(LivingEntity caster, LivingDamageEvent.Post event) {
            super(caster, event);
            this.event = event;
        }

        public LivingEntity getEntity() {
            return this.event.getEntity();
        }

        public float getOriginalDamage() {
            return this.event.getOriginalDamage();
        }

        public DamageSource getSource() {
            return this.event.getSource();
        }

        public float getNewDamage() {
            return this.event.getNewDamage();
        }

        public float getBlockedDamage() {
            return this.event.getBlockedDamage();
        }

        public float getShieldDamage() {
            return this.event.getShieldDamage();
        }

        public int getInvulnerabilityTicks() {
            return this.event.getPostAttackInvulnerabilityTicks();
        }

        public float getReduction(DamageContainer.Reduction reduction) {
            return this.event.getReduction(reduction);
        }
    }
}
