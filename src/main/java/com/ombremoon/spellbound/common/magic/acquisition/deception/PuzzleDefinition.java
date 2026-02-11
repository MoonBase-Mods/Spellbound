package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PuzzleDefinition(
        List<SpellAction> objectives,
        List<ResourceLocation> rules,
        List<ActionCriterion<?>> resetConditions,
        Optional<List<ResourceLocation>> alternativeConfigs
) {
    public static final Codec<PuzzleDefinition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SpellAction.CODEC.listOf().fieldOf("objectives").forGetter(PuzzleDefinition::objectives),
                    ResourceLocation.CODEC.listOf().fieldOf("rules").forGetter(PuzzleDefinition::rules),
                    ActionCriterion.CODEC.listOf().fieldOf("reset_conditions").forGetter(PuzzleDefinition::resetConditions),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("alternative_configs").forGetter(PuzzleDefinition::alternativeConfigs)
            ).apply(instance, PuzzleDefinition::new)
    );

    public List<SpellAction> getObjectives() {
        return objectives;
    }

    public boolean hasRule(ResourceLocation rule) {
        return rules.contains(rule);
    }

    public static class Builder {
        private final List<SpellAction> objectives = new ArrayList<>();
        private final List<ResourceLocation> rules = new ArrayList<>();
        private final List<ActionCriterion<?>> resetConditions = new ArrayList<>();
        private List<ResourceLocation> alternativeConfigs = new ArrayList<>();

        public static Builder configuration() {
            return new Builder();
        }

        public Builder withObjective(SpellAction.Builder action) {
            this.objectives.add(action.build());
            return this;
        }

        public Builder addRule(ResourceLocation rule) {
            this.rules.add(rule);
            return this;
        }

        public Builder resetOn(ActionCriterion<?> criterion) {
            this.resetConditions.add(criterion);
            return this;
        }

        public Builder withAlternativeConfig(ResourceLocation config) {
            this.alternativeConfigs.add(config);
            return this;
        }

        public PuzzleDefinition build() {
            return new PuzzleDefinition(objectives, rules, resetConditions, Optional.of(alternativeConfigs));
        }
    }
}
