package com.ombremoon.spellbound.common.world.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class WildMushroomFeatureConfiguration extends HugeMushroomFeatureConfiguration {
    public static final Codec<WildMushroomFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter(configuration -> configuration.capProvider),
            BlockStateProvider.CODEC.fieldOf("extra_cap_provider").forGetter(configuration -> configuration.extraCapProvider),
            BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter(configuration -> configuration.stemProvider),
            Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter(configuration -> configuration.foliageRadius)
    ).apply(instance, WildMushroomFeatureConfiguration::new));

    public final BlockStateProvider extraCapProvider;

    public WildMushroomFeatureConfiguration(BlockStateProvider capProvider, BlockStateProvider extraCapProvider, BlockStateProvider stemProvider, int foliageRadius) {
        super(capProvider, stemProvider, foliageRadius);
        this.extraCapProvider = extraCapProvider;
    }
}
