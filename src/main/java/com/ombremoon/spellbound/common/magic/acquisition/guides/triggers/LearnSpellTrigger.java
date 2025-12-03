package com.ombremoon.spellbound.common.magic.acquisition.guides.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.CuredZombieVillagerTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.SimpleTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.SpecialTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;
import java.util.function.Predicate;

public class LearnSpellTrigger extends SimpleTrigger<LearnSpellTrigger.Instance> {

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, SpellType<?> spell) {
        this.trigger(player, instance -> instance.matches(player, spell));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<SpellPredicate> spell) implements SimpleTrigger.Instance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                        SpellPredicate.CODEC.optionalFieldOf("spell").forGetter(Instance::spell)
                ).apply(instance, Instance::new)
        );

        public static ActionCriterion<Instance> learnSpell(SpellPredicate spellPredicate) {
            return SBTriggers.LEARN_SPELL.get()
                    .createCriterion(new Instance(Optional.empty(), Optional.ofNullable(spellPredicate)));
        }

        public boolean matches(ServerPlayer player, SpellType<?> spell) {
            return this.spell.get().matches(spell) &&
                    SpellUtil.getSpellHandler(player).getSpellList().contains(spell);
        }

        @Override
        public void validate(CriterionValidator validator) {
            SimpleTrigger.Instance.super.validate(validator);
        }
    }
}
