package com.ombremoon.spellbound.common.magic.acquisition.divine;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record SpellAction(ActionRewards rewards, Map<String, ActionCriterion<?>> criteria, ActionRequirements requirements, int cooldown) {
//    public static final ResourceKey<Registry<SpellAction>> REGISTRY = ResourceKey.createRegistryKey(CommonClass.customLocation("divine_action"));
    public static final Codec<Map<String, ActionCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, ActionCriterion.CODEC)
            .validate(map -> map.isEmpty() ? DataResult.error(() -> "Spell action cannot be empty") : DataResult.success(map));
    public static final Codec<SpellAction> CODEC = RecordCodecBuilder.<SpellAction>create(
            instance -> instance.group(
                    ActionRewards.CODEC.optionalFieldOf("rewards", ActionRewards.EMPTY).forGetter(SpellAction::rewards),
                    CRITERIA_CODEC.fieldOf("criteria").forGetter(SpellAction::criteria),
                    ActionRequirements.CODEC.optionalFieldOf("requirements").forGetter(action -> Optional.of(action.requirements())),
                    Codec.INT.optionalFieldOf("cooldown", 0).forGetter(SpellAction::cooldown)
            ).apply(instance, (actionRewards, criterionMap, actionRequirements, cooldown) -> {
                ActionRequirements requirements1 = actionRequirements.orElseGet(() -> ActionRequirements.allOf(criterionMap.keySet()));
                return new SpellAction(actionRewards, criterionMap, requirements1, cooldown);
            })
    ).validate(SpellAction::validate);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellAction> STREAM_CODEC = StreamCodec.ofMember(SpellAction::write, SpellAction::read);
    public static final Codec<Optional<WithConditions<SpellAction>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(CODEC);

    private static DataResult<SpellAction> validate(SpellAction action) {
        return action.requirements().validate(action.criteria().keySet()).map(requirements -> action);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        this.requirements.write(buffer);
        buffer.writeVarInt(this.cooldown);
    }

    private static SpellAction read(RegistryFriendlyByteBuf buffer) {
        return new SpellAction(ActionRewards.EMPTY, Map.of(), new ActionRequirements(buffer), buffer.readVarInt());
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
        private int cooldown;

        public static Builder action() {
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

        public Builder requirements(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public SpellAction build() {
            Map<String, ActionCriterion<?>> map = this.criteria.buildOrThrow();
            ActionRequirements requirements = this.requirements.orElseGet(() -> this.requirementsStrategy.create(map.keySet()));
            return new SpellAction(this.rewards, map, requirements, this.cooldown);
        }

        public ActionHolder build(ResourceLocation id) {
            Map<String, ActionCriterion<?>> map = this.criteria.buildOrThrow();
            ActionRequirements requirements = this.requirements.orElseGet(() -> this.requirementsStrategy.create(map.keySet()));
            return new ActionHolder(
                    id, new SpellAction(this.rewards, map, requirements, this.cooldown)
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
