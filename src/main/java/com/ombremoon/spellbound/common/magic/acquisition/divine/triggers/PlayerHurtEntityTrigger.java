package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class PlayerHurtEntityTrigger extends SimpleTrigger<PlayerHurtEntityTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Entity entity, DamageSource source, float amountDealt, float amountTaken, boolean blocked) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(player, lootcontext, source, amountDealt, amountTaken, blocked));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DamagePredicate> damage, Optional<ContextAwarePredicate> entity) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PlayerHurtEntityTrigger.TriggerInstance::player),
                        DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(PlayerHurtEntityTrigger.TriggerInstance::damage),
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(PlayerHurtEntityTrigger.TriggerInstance::entity)
                ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> playerHurtEntity() {
            return SBTriggers.PLAYER_HURT_ENTITY.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> playerHurtEntityWithDamage(Optional<DamagePredicate> damage) {
            return SBTriggers.PLAYER_HURT_ENTITY.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), damage, Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> playerHurtEntityWithDamage(DamagePredicate.Builder damage) {
            return SBTriggers.PLAYER_HURT_ENTITY.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(damage.build()), Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> playerHurtEntity(Optional<EntityPredicate> entity) {
            return SBTriggers.PLAYER_HURT_ENTITY.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), EntityPredicate.wrap(entity)));
        }

        public static ActionCriterion<TriggerInstance> playerHurtEntity(
                Optional<DamagePredicate> damage, Optional<EntityPredicate> entity
        ) {
            return SBTriggers.PLAYER_HURT_ENTITY.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), damage, EntityPredicate.wrap(entity)));
        }

        public static ActionCriterion<TriggerInstance> playerHurtEntity(
                DamagePredicate.Builder damage, Optional<EntityPredicate> entity
        ) {
            return SBTriggers.PLAYER_HURT_ENTITY.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(damage.build()), EntityPredicate.wrap(entity)));
        }

        public static ActionCriterion<TriggerInstance> deflectAndHitProjectile(EntityPredicate.Builder entity) {
            return SBTriggers.DEFLECT_AND_HIT_PROJECTILE.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(entity.build()))));
        }

        public boolean matches(ServerPlayer player, LootContext context, DamageSource damage, float dealt, float taken, boolean blocked) {
            if (this.damage.isPresent() && !this.damage.get().matches(player, damage, dealt, taken, blocked)) {
                return false;
            }

            return this.entity.isEmpty() || this.entity.get().matches(context);
        }

        @Override
        public void validate(CriterionValidator validator) {
            SimpleTrigger.Instance.super.validate(validator);
            validator.validateEntity(this.entity, ".entity");
        }
    }
}
