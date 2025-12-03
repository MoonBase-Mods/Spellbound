package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.worldgen.WildMushroomFeatureConfiguration;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public interface SBConfiguredFeatures {
    ResourceKey<ConfiguredFeature<?, ?>> FIRE_GEODE = register("fire_geode");
    ResourceKey<ConfiguredFeature<?, ?>> ICE_GEODE = register("ice_geode");
    ResourceKey<ConfiguredFeature<?, ?>> SHOCK_GEODE = register("shock_geode");
    ResourceKey<ConfiguredFeature<?, ?>> HUGE_WILD_MUSHROOM = register("huge_wild_mushroom");

    private static ResourceKey<ConfiguredFeature<?, ?>> register(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, CommonClass.customLocation(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(
                context,
                FIRE_GEODE,
                Feature.GEODE,
                new GeodeConfiguration(
                        new GeodeBlockSettings(
                                BlockStateProvider.simple(Blocks.AIR),
                                BlockStateProvider.simple(SBBlocks.SMOLDERING_CRYSTAL_BLOCK.get()),
                                BlockStateProvider.simple(SBBlocks.BUDDING_SMOLDERING_CRYSTAL.get()),
                                BlockStateProvider.simple(Blocks.CALCITE),
                                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                                List.of(
                                        SBBlocks.SMALL_SMOLDERING_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.MEDIUM_SMOLDERING_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.LARGE_SMOLDERING_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.SMOLDERING_CRYSTAL_CLUSTER.get().defaultBlockState()
                                ),
                                BlockTags.FEATURES_CANNOT_REPLACE,
                                BlockTags.GEODE_INVALID_BLOCKS
                        ),
                        new GeodeLayerSettings(1.7, 2.2, 3.2, 4.2),
                        new GeodeCrackSettings(0.95, 2.0, 2),
                        0.35,
                        0.083,
                        true,
                        UniformInt.of(4, 6),
                        UniformInt.of(3, 4),
                        UniformInt.of(1, 2),
                        -16,
                        16,
                        0.05,
                        1

                )
        );
        register(
                context,
                ICE_GEODE,
                Feature.GEODE,
                new GeodeConfiguration(
                        new GeodeBlockSettings(
                                BlockStateProvider.simple(Blocks.AIR),
                                BlockStateProvider.simple(SBBlocks.FROZEN_CRYSTAL_BLOCK.get()),
                                BlockStateProvider.simple(SBBlocks.BUDDING_FROZEN_CRYSTAL.get()),
                                BlockStateProvider.simple(Blocks.CALCITE),
                                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                                List.of(
                                        SBBlocks.SMALL_FROZEN_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.MEDIUM_FROZEN_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.LARGE_FROZEN_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.FROZEN_CRYSTAL_CLUSTER.get().defaultBlockState()
                                ),
                                BlockTags.FEATURES_CANNOT_REPLACE,
                                BlockTags.GEODE_INVALID_BLOCKS
                        ),
                        new GeodeLayerSettings(1.7, 2.2, 3.2, 4.2),
                        new GeodeCrackSettings(0.95, 2.0, 2),
                        0.35,
                        0.083,
                        true,
                        UniformInt.of(4, 6),
                        UniformInt.of(3, 4),
                        UniformInt.of(1, 2),
                        -16,
                        16,
                        0.05,
                        1

                )
        );
        register(
                context,
                SHOCK_GEODE,
                Feature.GEODE,
                new GeodeConfiguration(
                        new GeodeBlockSettings(
                                BlockStateProvider.simple(Blocks.AIR),
                                BlockStateProvider.simple(SBBlocks.STORM_CRYSTAL_BLOCK.get()),
                                BlockStateProvider.simple(SBBlocks.BUDDING_STORM_CRYSTAL.get()),
                                BlockStateProvider.simple(Blocks.CALCITE),
                                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                                List.of(
                                        SBBlocks.SMALL_STORM_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.MEDIUM_STORM_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.LARGE_STORM_CRYSTAL_BUD.get().defaultBlockState(),
                                        SBBlocks.STORM_CRYSTAL_CLUSTER.get().defaultBlockState()
                                ),
                                BlockTags.FEATURES_CANNOT_REPLACE,
                                BlockTags.GEODE_INVALID_BLOCKS
                        ),
                        new GeodeLayerSettings(1.7, 2.2, 3.2, 4.2),
                        new GeodeCrackSettings(0.95, 2.0, 2),
                        0.35,
                        0.083,
                        true,
                        UniformInt.of(4, 6),
                        UniformInt.of(3, 4),
                        UniformInt.of(1, 2),
                        -16,
                        16,
                        0.05,
                        1

                )
        );
        register(
                context,
                HUGE_WILD_MUSHROOM,
                SBFeatures.HUGE_WILD_MUSHROOM.get(),
                new WildMushroomFeatureConfiguration(
                        BlockStateProvider.simple(SBBlocks.PURPLE_SPORE_BLOCK.get().defaultBlockState().setValue(HugeMushroomBlock.DOWN, false)),
                        BlockStateProvider.simple(SBBlocks.GREEN_SPORE_BLOCK.get().defaultBlockState().setValue(HugeMushroomBlock.DOWN, false)),
                        BlockStateProvider.simple(
                                Blocks.MUSHROOM_STEM
                                        .defaultBlockState()
                                        .setValue(HugeMushroomBlock.UP, false)
                                        .setValue(HugeMushroomBlock.DOWN, false)
                        ),
                        2
                )
        );
    }
}
