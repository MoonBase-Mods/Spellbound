package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class BuddingCrystalBlock extends CrystalBlock {
    public static final MapCodec<BuddingCrystalBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    CrystalType.CODEC.fieldOf("crystal").forGetter(block -> block.crystal),
                    propertiesCodec()
            ).apply(instance, BuddingCrystalBlock::new)
    );
    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public BuddingCrystalBlock(CrystalType crystal, Properties properties) {
        super(crystal, properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            Block block = null;
            if (BuddingAmethystBlock.canClusterGrowAtState(blockstate)) {
                block = this.crystal.getSmallCluster().get();
            } else if (blockstate.is(this.crystal.getSmallCluster().get()) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
                block = this.crystal.getMediumCluster().get();
            } else if (blockstate.is(this.crystal.getMediumCluster().get()) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
                block = this.crystal.getLargeCluster().get();
            } else if (blockstate.is(this.crystal.getLargeCluster().get()) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
                block = this.crystal.getCluster().get();
            }

            if (block != null) {
                BlockState blockstate1 = block.defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, direction)
                        .setValue(CrystalClusterBlock.WATERLOGGED, Boolean.valueOf(blockstate.getFluidState().getType() == Fluids.WATER));
                level.setBlockAndUpdate(blockpos, blockstate1);
            }
        }
    }
}
