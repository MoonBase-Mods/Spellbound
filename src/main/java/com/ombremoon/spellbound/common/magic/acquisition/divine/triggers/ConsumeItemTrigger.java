package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class ConsumeItemTrigger extends SimpleTrigger<ConsumeItemTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack item) {
        this.trigger(player, instance -> instance.matches(item));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ConsumeItemTrigger.TriggerInstance::item)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> usedItem() {
            return SBTriggers.CONSUME_ITEM.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> usedItem(ItemLike item) {
            return usedItem(ItemPredicate.Builder.item().of(item.asItem()));
        }

        public static ActionCriterion<TriggerInstance> usedItem(ItemPredicate.Builder item) {
            return SBTriggers.CONSUME_ITEM.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(item.build())));
        }

        public boolean matches(ItemStack item) {
            return this.item.isEmpty() || this.item.get().test(item);
        }
    }
}
