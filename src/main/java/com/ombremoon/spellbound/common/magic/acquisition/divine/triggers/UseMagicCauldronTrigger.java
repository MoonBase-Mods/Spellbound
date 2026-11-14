package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import com.ombremoon.spellbound.common.world.item.PhialItem;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class UseMagicCauldronTrigger extends SimpleTrigger<UseMagicCauldronTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack, PhialItem.PhialType type) {
        this.trigger(player, instance -> instance.matches(stack, type));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<PhialItem.PhialType> type) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item),
                        PhialItem.PhialType.CODEC.optionalFieldOf("type").forGetter(TriggerInstance::type)
                ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> blessItem(ItemPredicate.Builder item, PhialItem.PhialType type) {
            return SBTriggers.USE_HOLY_CAULDRON.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(item.build()), Optional.of(type)));
        }

        public static ActionCriterion<TriggerInstance> blessItem(ItemLike item, PhialItem.PhialType type) {
            return blessItem(ItemPredicate.Builder.item().of(item), type);
        }

        public boolean matches(ItemStack itemStack, PhialItem.PhialType type) {
            if (this.item.isPresent() && !this.item.get().test(itemStack)) {
                return false;
            }

            return this.type.isEmpty() || this.type.get() == type;
        }
    }
}
