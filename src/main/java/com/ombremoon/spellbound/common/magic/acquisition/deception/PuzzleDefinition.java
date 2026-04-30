package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.DynamicLevelSpawnData;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record PuzzleDefinition(
        ResourceLocation puzzleId,
        List<SpellAction> objectives,
        List<DungeonRule<?>> rules,
        List<ResourceLocation> flags,
        List<SpellAction> resetConditions,
        int maxResetCount,
        DynamicLevelSpawnData spawnData
) {
    public static final PuzzleDefinition DEFAULT = new PuzzleDefinition(CommonClass.customLocation("default"), List.of(), List.of(), List.of(), List.of(), 0, DynamicLevelSpawnData.DEFAULT);
    public static final Codec<PuzzleDefinition> CODEC = RecordCodecBuilder.<PuzzleDefinition>create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("puzzle_id").forGetter(PuzzleDefinition::puzzleId),
                    SpellAction.CODEC.listOf().fieldOf("objectives").forGetter(PuzzleDefinition::objectives),
                    DungeonRule.CODEC.listOf().fieldOf("rules").forGetter(PuzzleDefinition::rules),
                    ResourceLocation.CODEC.listOf().fieldOf("flags").forGetter(PuzzleDefinition::flags),
                    SpellAction.CODEC.listOf().fieldOf("reset_conditions").forGetter(PuzzleDefinition::resetConditions),
                    Codec.INT.fieldOf("max_reset_count").forGetter(PuzzleDefinition::maxResetCount),
                    DynamicLevelSpawnData.CODEC.fieldOf("spawn_data").forGetter(PuzzleDefinition::spawnData)
            ).apply(instance, PuzzleDefinition::new)
    ).validate(PuzzleDefinition::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, PuzzleDefinition> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, PuzzleDefinition::puzzleId,
            DungeonRule.STREAM_CODEC.apply(ByteBufCodecs.list()), PuzzleDefinition::rules,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), PuzzleDefinition::flags,
            ByteBufCodecs.VAR_INT, PuzzleDefinition::maxResetCount,
            PuzzleDefinition::forStreamCodec
    );

    private static DataResult<PuzzleDefinition> validate(PuzzleDefinition properties) {
        for (DungeonRule<?> rule : properties.rules()) {
            if (!RuleType.isRule(rule.rule().id()))
                return DataResult.error(() -> "Invalid rule: " + rule);
        }

        for (ResourceLocation flag : properties.flags()) {
            if (!ResetFlags.isFlag(flag))
                return DataResult.error(() -> "Invalid flag: " + flag);
        }

        return DataResult.success(properties);
    }

    public List<SpellAction> getObjectives() {
        return objectives;
    }

    @SuppressWarnings("unchecked")
    public <T> DungeonRule<T> getRule(RuleType<T> rule) {
        return (DungeonRule<T>) this.rules.stream().filter(r -> r.rule() == rule).findFirst().orElse(null);
    }

    public boolean hasRule(RuleType<?> rule) {
        var rules = this.rules.stream().map(DungeonRule::rule).toList();
        return rules.contains(rule);
    }

    public boolean hasFlag(ResourceLocation rule) {
        return flags.contains(rule);
    }

    public static PuzzleDefinition forStreamCodec(ResourceLocation location, List<DungeonRule<?>> rules, List<ResourceLocation> flags, int maxResetCount) {
        return new PuzzleDefinition(location, List.of(), rules, flags, List.of(), maxResetCount, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof PuzzleDefinition definition && this.puzzleId.equals(definition.puzzleId);
        }
    }

    @Override
    public int hashCode() {
        return this.puzzleId.hashCode();
    }

    public static class Builder {
        private ResourceLocation puzzleId;
        private final List<SpellAction> objectives = new ArrayList<>();
        private final List<DungeonRule<?>> rules = new ArrayList<>();
        private final List<ResourceLocation> flags = new ArrayList<>();
        private final List<SpellAction> resetConditions = new ArrayList<>();
        private int maxResetCount = 3;
        private DynamicLevelSpawnData spawnData = DynamicLevelSpawnData.DEFAULT;

        public static Builder define(ResourceLocation puzzleId) {
            Builder builder = new Builder();
            builder.puzzleId = puzzleId;
            return builder;
        }

        public Builder withObjective(SpellAction.Builder action) {
            this.objectives.add(action.build());
            return this;
        }

        public Builder addRule(RuleType<?> rule) {
            this.rules.add(new DungeonRule<>(rule, List.of()));
            return this;
        }

        @SafeVarargs
        public final <T> Builder addRuleWithException(RuleType<T> rule, T... exceptions) {
            this.rules.add(new DungeonRule<>(rule, List.of(exceptions)));
            return this;
        }

        public Builder withFlag(ResourceLocation flag) {
            this.flags.add(flag);
            return this;
        }

        public Builder resetOn(SpellAction.Builder action) {
            this.resetConditions.add(action.build());
            return this;
        }

        public Builder maxResetCount(int count) {
            this.maxResetCount = count;
            return this;
        }

        public Builder spawnData(DynamicLevelSpawnData.Builder spawnData) {
            this.spawnData = spawnData.build();
            return this;
        }

        public PuzzleDefinition build() {
            return new PuzzleDefinition(puzzleId, objectives, rules, flags, resetConditions, maxResetCount, this.spawnData);
        }
    }
}
