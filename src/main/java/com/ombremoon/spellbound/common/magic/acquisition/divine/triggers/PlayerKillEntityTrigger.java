package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class PlayerKillEntityTrigger extends SimpleTrigger<PlayerKillEntityTrigger.Instance> {

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, Entity entity, DamageSource source, int numKilled) {
        LootContext context = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(player, context, source, numKilled));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate, Optional<DamageSourcePredicate> killingBlow, MinMaxBounds.Ints numKilled) implements SimpleTrigger.Instance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(Instance::entityPredicate),
                        DamageSourcePredicate.CODEC.optionalFieldOf("killing_blow").forGetter(Instance::killingBlow),
                        MinMaxBounds.Ints.CODEC.optionalFieldOf("number_entities_killed", MinMaxBounds.Ints.ANY).forGetter(Instance::numKilled)
        ).apply(instance, Instance::new));

        public static ActionCriterion<Instance> playerKilledEntity(Optional<EntityPredicate> entityPredicate) {
            return SBTriggers.PLAYER_KILL.get().createCriterion(new Instance(Optional.empty(), EntityPredicate.wrap(entityPredicate), Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public static ActionCriterion<Instance> playerKilledEntity(EntityPredicate.Builder entityPredicate) {
            return SBTriggers.PLAYER_KILL.get().createCriterion(new Instance(Optional.empty(), Optional.of(EntityPredicate.wrap(entityPredicate)), Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public static ActionCriterion<Instance> playerKilledUndead(EntityPredicate.Builder entityPredicate, MinMaxBounds.Ints numKilled) {
            return SBTriggers.KILL_UNDEAD.get().createCriterion(new Instance(Optional.empty(), Optional.of(EntityPredicate.wrap(entityPredicate)), Optional.empty(), numKilled));
        }

        public static ActionCriterion<Instance> playerKilledVillager(EntityPredicate.Builder entityPredicate, MinMaxBounds.Ints numKilled) {
            return SBTriggers.KILL_VILLAGER.get().createCriterion(new Instance(Optional.empty(), Optional.of(EntityPredicate.wrap(entityPredicate)), Optional.empty(), numKilled));
        }

        public boolean matches(ServerPlayer player, LootContext context, DamageSource source, int numKilled) {
            if (this.killingBlow.isPresent() && !this.killingBlow.get().matches(player, source)) {
                return false;
            }
            return (this.entityPredicate.isEmpty() || this.entityPredicate.get().matches(context)) && this.numKilled.matches(numKilled);
        }

        @Override
        public void validate(CriterionValidator validator) {
            SimpleTrigger.Instance.super.validate(validator);
            validator.validateEntity(this.entityPredicate, ".entity");
        }
    }
}
