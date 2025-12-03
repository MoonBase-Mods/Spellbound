package com.ombremoon.spellbound.common.world.worldgen;

import com.mojang.serialization.Codec;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ArcanthusFeature extends Feature<ArcanthusConfig> {

    public ArcanthusFeature(Codec<ArcanthusConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ArcanthusConfig> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState blockBelow = level.getBlockState(origin.below());
        ArcanthusConfig config = context.config();
        RandomSource random = context.random();
        if (!blockBelow.is(SBTags.Blocks.ARCANTHUS_GROWTH_BLOCKS)) {
            return false;
        }

        int y = origin.getY();

        int j = 0;
        for (int k = 0; k < config.spreadWidth * config.spreadWidth && j < config.count; k++) {
            BlockPos pos = origin.offset(random.nextInt(config.spreadWidth) - random.nextInt(config.spreadWidth), random.nextInt(config.spreadHeight) - random.nextInt(config.spreadHeight), random.nextInt(config.spreadWidth) - random.nextInt(config.spreadWidth));
            BlockState state = SBBlocks.ARCANTHUS.get().defaultBlockState();
            if (level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight() && state.canSurvive(level, pos) && level.getBlockState(pos.below()).is(SBTags.Blocks.ARCANTHUS_GROWTH_BLOCKS)) {
                level.setBlock(pos, state, 2);
                j++;
            }
        }

        return j > 0;
    }
}
