package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class DivineTrigger extends SimpleTrigger<DivineTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
                ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> blessShrine() {
            return SBTriggers.BLESS_SHRINE.get().createCriterion(new TriggerInstance(Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> curseShrine() {
            return SBTriggers.CURSE_SHRINE.get().createCriterion(new TriggerInstance(Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> useHalfFilledSiphon() {
            return SBTriggers.USE_HALF_FILLED_SIPHON.get().createCriterion(new TriggerInstance(Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> decoratedShrine() {
            return SBTriggers.DECORATED_SHRINE.get().createCriterion(new TriggerInstance(Optional.empty()));
        }
    }
}
