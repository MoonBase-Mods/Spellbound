package com.ombremoon.spellbound.common.world.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.tslat.smartbrainlib.util.RandomUtil;

public class HugeWildMushroomFeature extends Feature<WildMushroomFeatureConfiguration> {

    public HugeWildMushroomFeature(Codec<WildMushroomFeatureConfiguration> codec) {
        super(codec);
    }

    protected void placeTrunk(LevelAccessor level, RandomSource random, BlockPos pos, HugeMushroomFeatureConfiguration config, int maxHeight, BlockPos.MutableBlockPos mutablePos) {
        for(int i = 0; i < maxHeight; ++i) {
            mutablePos.set(pos).move(Direction.UP, i);
            if (!level.getBlockState(mutablePos).isSolidRender(level, mutablePos)) {
                this.setBlock(level, mutablePos, config.stemProvider.getState(random, pos));
            }
        }

    }

    protected int getTreeHeight(RandomSource random) {
        int i = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            i *= 2;
        }

        return i;
    }

    protected boolean isValidPosition(LevelAccessor level, BlockPos pos, int maxHeight, BlockPos.MutableBlockPos mutablePos, HugeMushroomFeatureConfiguration config) {
        int i = pos.getY();
        if (i >= level.getMinBuildHeight() + 1 && i + maxHeight + 1 < level.getMaxBuildHeight()) {
            BlockState blockstate = level.getBlockState(pos.below());
            if (!isDirt(blockstate) && !blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
                return false;
            } else {
                for(int j = 0; j <= maxHeight; ++j) {
                    int k = this.getTreeRadiusForHeight(-1, -1, config.foliageRadius, j);

                    for(int l = -k; l <= k; ++l) {
                        for(int i1 = -k; i1 <= k; ++i1) {
                            BlockState blockstate1 = level.getBlockState(mutablePos.setWithOffset(pos, l, j, i1));
                            if (!blockstate1.isAir() && !blockstate1.is(BlockTags.LEAVES)) {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<WildMushroomFeatureConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        RandomSource randomsource = context.random();
        WildMushroomFeatureConfiguration wildMushroomFeatureConfiguration = context.config();
        int i = this.getTreeHeight(randomsource);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        if (!this.isValidPosition(worldgenlevel, blockpos, i, blockpos$mutableblockpos, wildMushroomFeatureConfiguration)) {
            return false;
        } else {
            this.makeCap(worldgenlevel, randomsource, blockpos, i, blockpos$mutableblockpos, wildMushroomFeatureConfiguration);
            this.placeTrunk(worldgenlevel, randomsource, blockpos, wildMushroomFeatureConfiguration, i, blockpos$mutableblockpos);
            return true;
        }
    }

    protected void makeCap(LevelAccessor levelAccessor, RandomSource randomSource, BlockPos blockPos, int maxHeight, BlockPos.MutableBlockPos mutableBlockPos, WildMushroomFeatureConfiguration config) {
        for(int i = maxHeight - 3; i <= maxHeight; ++i) {
            int j = i < maxHeight ? config.foliageRadius : config.foliageRadius - 1;
            int k = config.foliageRadius - 2;

            for(int l = -j; l <= j; ++l) {
                for(int i1 = -j; i1 <= j; ++i1) {
                    boolean flag = l == -j;
                    boolean flag1 = l == j;
                    boolean flag2 = i1 == -j;
                    boolean flag3 = i1 == j;
                    boolean flag4 = flag || flag1;
                    boolean flag5 = flag2 || flag3;
                    if (i >= maxHeight || flag4 != flag5) {
                        mutableBlockPos.setWithOffset(blockPos, l, i, i1);
                        if (!levelAccessor.getBlockState(mutableBlockPos).isSolidRender(levelAccessor, mutableBlockPos)) {
                            BlockState blockstate = config.capProvider.getState(randomSource, blockPos);
                            if (RandomUtil.percentChance(0.25F))
                                blockstate = config.extraCapProvider.getState(randomSource, blockPos);

                            if (blockstate.hasProperty(HugeMushroomBlock.WEST) && blockstate.hasProperty(HugeMushroomBlock.EAST) && blockstate.hasProperty(HugeMushroomBlock.NORTH) && blockstate.hasProperty(HugeMushroomBlock.SOUTH) && blockstate.hasProperty(HugeMushroomBlock.UP)) {
                                blockstate = blockstate.setValue(HugeMushroomBlock.UP, i >= maxHeight - 1).setValue(HugeMushroomBlock.WEST, l < -k).setValue(HugeMushroomBlock.EAST, l > k).setValue(HugeMushroomBlock.NORTH, i1 < -k).setValue(HugeMushroomBlock.SOUTH, i1 > k);
                            }

                            this.setBlock(levelAccessor, mutableBlockPos, blockstate);
                        }
                    }
                }
            }
        }
    }

    protected int getTreeRadiusForHeight(int i, int i1, int i2, int i3) {
        int j = 0;
        if (i3 < i1 && i3 >= i1 - 3) {
            j = i2;
        } else if (i3 == i1) {
            j = i2;
        }

        return j;
    }
}
