package com.ombremoon.spellbound.common.world.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ArcanthusConfig implements FeatureConfiguration {
    public static final Codec<ArcanthusConfig> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter(config -> config.spreadWidth),
                    ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter(config -> config.spreadHeight),
                    ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(config -> config.count)
            ).apply(inst, ArcanthusConfig::new));

    public final int spreadWidth;
    public final int spreadHeight;
    public final int count;

    public ArcanthusConfig(int spreadWidth, int spreadHeight, int count) {
        this.spreadWidth = spreadWidth;
        this.spreadHeight = spreadHeight;
        this.count = count;
    }
}