package com.ombremoon.spellbound.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Range(int min, int max) {

    public static final Codec<Range> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("min").forGetter(Range::min),
            Codec.INT.fieldOf("max").forGetter(Range::max)
    ).apply(inst, Range::new));

    public String toFormattedString() {
        return min == max ? String.valueOf(min) : min + "-" + max;
    }

    public static Range of(int min, int max) {
        return new Range(min, max);
    }

    @Override
    public String toString() {
        return "Range{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
