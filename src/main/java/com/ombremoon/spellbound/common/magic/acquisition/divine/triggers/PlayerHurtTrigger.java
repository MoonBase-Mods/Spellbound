package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Optional;

public class PlayerHurtTrigger extends SimpleTrigger<PlayerHurtTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, DamageSource source, float dealtDamage, float takenDamage, boolean blocked) {
        this.trigger(player, instance -> instance.matches(player, source, dealtDamage, takenDamage, blocked));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<DamagePredicate> damage) implements SimpleTrigger.Instance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PlayerHurtTrigger.Instance::player),
                            DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(PlayerHurtTrigger.Instance::damage)
                    )
                    .apply(instance, PlayerHurtTrigger.Instance::new)
        );

        public static ActionCriterion<Instance> entityHurtPlayer() {
            return SBTriggers.PLAYER_HURT.get().createCriterion(new Instance(Optional.empty(), Optional.empty()));
        }

        public static ActionCriterion<Instance> entityHurtPlayer(DamagePredicate damage) {
            return SBTriggers.PLAYER_HURT.get().createCriterion(new Instance(Optional.empty(), Optional.of(damage)));
        }

        public static ActionCriterion<Instance> entityHurtPlayer(DamagePredicate.Builder damage) {
            return SBTriggers.PLAYER_HURT.get()
                    .createCriterion(new Instance(Optional.empty(), Optional.of(damage.build())));
        }

        public boolean matches(ServerPlayer player, DamageSource source, float dealtDamage, float takenDamage, boolean blocked) {
            return !this.damage.isPresent() || this.damage.get().matches(player, source, dealtDamage, takenDamage, blocked);
        }
    }
}
