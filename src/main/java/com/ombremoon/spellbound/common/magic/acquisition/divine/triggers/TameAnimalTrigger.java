package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class TameAnimalTrigger extends SimpleTrigger<TameAnimalTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Animal entity) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(lootcontext));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entity) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)
                ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> tamedAnimal() {
            return SBTriggers.TAME_ANIMAL.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> tamedAnimal(EntityPredicate.Builder entity) {
            return SBTriggers.TAME_ANIMAL.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity))));
        }

        public boolean matches(LootContext lootContext) {
            return this.entity.isEmpty() || this.entity.get().matches(lootContext);
        }

        @Override
        public void validate(CriterionValidator validator) {
            Instance.super.validate(validator);
            validator.validateEntity(this.entity, ".entity");
        }
    }
}
