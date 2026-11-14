package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class SkillBuffTrigger extends SimpleTrigger<SkillBuffTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, SkillBuff<?> skillBuff, Entity source) {
        this.trigger(player, instance -> instance.matches(player, skillBuff, source));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<SkillBuffPredicate> skillBuff, Optional<EntityPredicate> source) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        SkillBuffPredicate.CODEC.codec().optionalFieldOf("skill_buff").forGetter(TriggerInstance::skillBuff),
                        EntityPredicate.CODEC.optionalFieldOf("source").forGetter(TriggerInstance::source)
                        ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> receivedSkillBuff(SkillBuffPredicate skillBuff, EntityPredicate.Builder source) {
            return SBTriggers.RECEIVED_SKILL_BUFF.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(skillBuff), Optional.of(source.build())));
        }

        public static ActionCriterion<TriggerInstance> receivedSkillBuff(SkillBuffPredicate skillBuff) {
            return SBTriggers.RECEIVED_SKILL_BUFF.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(skillBuff), Optional.empty()));
        }

        public static ActionCriterion<TriggerInstance> buffSelf(SkillBuffPredicate skillBuff, EntityPredicate.Builder source) {
            return SBTriggers.BUFF_SELF.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(skillBuff), Optional.of(source.build())));
        }

        public static ActionCriterion<TriggerInstance> buffSelf(SkillBuffPredicate skillBuff) {
            return SBTriggers.BUFF_SELF.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(skillBuff), Optional.empty()));
        }

        public boolean matches(ServerPlayer player, SkillBuff<?> skillBuff, Entity source) {
            if (this.skillBuff.isPresent()) {
                if (!this.skillBuff.get().matches(skillBuff)) {
                    return false;
                }

                return this.source.isEmpty() || this.source.get().matches(player.serverLevel(), player.position(), source);
            }

            return false;
        }
    }
}
