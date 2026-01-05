package com.ombremoon.spellbound.common.magic.acquisition.divine;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record DivineAction(ActionRewards rewards, Map<String, ActionCriterion<?>> criteria, ActionRequirements requirements) {
    public static final ResourceKey<Registry<DivineAction>> REGISTRY = ResourceKey.createRegistryKey(CommonClass.customLocation("divine_action"));
    public static final Codec<Map<String, ActionCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, ActionCriterion.CODEC)
            .validate(map -> map.isEmpty() ? DataResult.error(() -> "Divine action cannot be empty") : DataResult.success(map));
    public static final Codec<DivineAction> CODEC = RecordCodecBuilder.<DivineAction>create(
            instance -> instance.group(
                    ActionRewards.CODEC.optionalFieldOf("rewards", ActionRewards.EMPTY).forGetter(DivineAction::rewards),
                    CRITERIA_CODEC.fieldOf("criteria").forGetter(DivineAction::criteria),
                    ActionRequirements.CODEC.optionalFieldOf("requirements").forGetter(action -> Optional.of(action.requirements()))
            ).apply(instance, (actionRewards, criterionMap, actionRequirements) -> {
                ActionRequirements requirements1 = actionRequirements.orElseGet(() -> ActionRequirements.allOf(criterionMap.keySet()));
                return new DivineAction(actionRewards, criterionMap, requirements1);
            })
    ).validate(DivineAction::validate);
    public static final StreamCodec<RegistryFriendlyByteBuf, DivineAction> STREAM_CODEC = StreamCodec.ofMember(DivineAction::write, DivineAction::read);
    public static final Codec<Optional<WithConditions<DivineAction>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(CODEC);

    private static DataResult<DivineAction> validate(DivineAction action) {
        return action.requirements().validate(action.criteria().keySet()).map(requirements -> action);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        this.requirements.write(buffer);
    }

    private static DivineAction read(RegistryFriendlyByteBuf buffer) {
        return new DivineAction(ActionRewards.EMPTY, Map.of(), new ActionRequirements(buffer));
    }

    public void validate(ProblemReporter reporter, HolderGetter.Provider lootData) {
        this.criteria.forEach((s, actionCriterion) -> {
            var validator = new CriterionValidator(reporter.forChild(s), lootData);
            actionCriterion.triggerInstance().validate(validator);
        });
    }

    public static class Builder {
        private ActionRewards rewards = ActionRewards.EMPTY;
        private final ImmutableMap.Builder<String, ActionCriterion<?>> criteria = ImmutableMap.builder();
        private Optional<ActionRequirements> requirements = Optional.empty();
        private ActionRequirements.Strategy requirementsStrategy = ActionRequirements.Strategy.AND;

        public static Builder divineAction() {
            return new Builder();
        }

        public Builder rewards(ActionRewards.Builder rewardsBuilder) {
            return this.rewards(rewardsBuilder.build());
        }

        public Builder rewards(ActionRewards rewards) {
            this.rewards = rewards;
            return this;
        }

        public Builder addCriterion(String key, ActionCriterion<?> criterion) {
            this.criteria.put(key, criterion);
            return this;
        }

        public Builder requirements(ActionRequirements.Strategy requirementsStrategy) {
            this.requirementsStrategy = requirementsStrategy;
            return this;
        }

        public Builder requirements(ActionRequirements requirements) {
            this.requirements = Optional.of(requirements);
            return this;
        }

        public DivineAction build() {
            Map<String, ActionCriterion<?>> map = this.criteria.buildOrThrow();
            ActionRequirements requirements = this.requirements.orElseGet(() -> this.requirementsStrategy.create(map.keySet()));
            return new DivineAction(this.rewards, map, requirements);
        }

        public ActionHolder build(ResourceLocation id) {
            Map<String, ActionCriterion<?>> map = this.criteria.buildOrThrow();
            ActionRequirements requirements = this.requirements.orElseGet(() -> this.requirementsStrategy.create(map.keySet()));
            return new ActionHolder(
                    id, new DivineAction(this.rewards, map, requirements)
            );
        }

        public ActionHolder save(Consumer<ActionHolder> output, String id) {
            return this.save(output, ResourceLocation.parse(id));
        }

        public ActionHolder save(Consumer<ActionHolder> output, ResourceLocation location) {
            ActionHolder actionHolder = this.build(location);
            output.accept(actionHolder);
            return actionHolder;
        }
    }
}
