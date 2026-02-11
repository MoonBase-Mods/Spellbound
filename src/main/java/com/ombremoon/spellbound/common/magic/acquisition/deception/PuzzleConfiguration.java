package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record PuzzleConfiguration(List<PuzzleDefinition> puzzles) {
    public static final Codec<PuzzleConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    PuzzleDefinition.CODEC.listOf().fieldOf("puzzles").forGetter(PuzzleConfiguration::puzzles)
            ).apply(instance, PuzzleConfiguration::new)
    );
}
