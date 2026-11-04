package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class ItemUsedOnEntityTrigger extends SimpleTrigger<ItemUsedOnEntityTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack item, Entity entity) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, (instance) -> instance.matches(item, lootcontext));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item),
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)
                ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> itemUsedOnEntity(Optional<ContextAwarePredicate> player, ItemPredicate.Builder item, Optional<ContextAwarePredicate> entity) {
            return SBTriggers.USE_ITEM_ON_ENTITY.get().createCriterion(new TriggerInstance(player, Optional.of(item.build()), entity));
        }

        public static ActionCriterion<TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder item, Optional<ContextAwarePredicate> entity) {
            return itemUsedOnEntity(Optional.empty(), item, entity);
        }

        public static ActionCriterion<TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder item) {
            return itemUsedOnEntity(item, Optional.empty());
        }

        public boolean matches(ItemStack item, LootContext lootContext) {
            return (this.item.isEmpty() || this.item.get().test(item)) && (this.entity.isEmpty() || this.entity.get().matches(lootContext));
        }

        @Override
        public void validate(CriterionValidator validator) {
            SimpleTrigger.Instance.super.validate(validator);
            validator.validateEntity(this.entity, ".entity");
        }
    }
}
