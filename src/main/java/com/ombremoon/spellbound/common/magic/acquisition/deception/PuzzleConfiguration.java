package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Instrument;

import java.util.ArrayList;
import java.util.List;

public record PuzzleConfiguration(List<PuzzleDefinition> puzzles) {
    public static final Codec<PuzzleConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    PuzzleDefinition.CODEC.listOf().fieldOf("puzzles").forGetter(PuzzleConfiguration::puzzles)
            ).apply(instance, PuzzleConfiguration::new)
    );

    public int getPuzzleCount() {
        return this.puzzles.size();
    }

    public int getPuzzleIndex(PuzzleDefinition definition) {
        return this.puzzles.indexOf(definition);
    }

    public static class Builder {
        private final List<PuzzleDefinition> puzzles = new ArrayList<>();

        public static Builder configuration() {
            return new Builder();
        }

        public Builder addPuzzle(PuzzleDefinition.Builder puzzle) {
            this.puzzles.add(puzzle.build());
            return this;
        }

        public PuzzleConfiguration build() {
            return new PuzzleConfiguration(puzzles);
        }
    }
}
