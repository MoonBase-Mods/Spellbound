package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumCarpetBlock extends CarpetBlock {
    public static final MapCodec<MyceliumCarpetBlock> CODEC = simpleCodec(MyceliumCarpetBlock::new);

    public MyceliumCarpetBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) || level.getBlockState(pos.below()).is(Blocks.DIRT))
            level.setBlock(pos.below(), Blocks.MYCELIUM.defaultBlockState(), 3);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(10) == 0) {
            level.addParticle(
                    ParticleTypes.MYCELIUM,
                    (double)pos.getX() + random.nextDouble(),
                    (double)pos.getY() + 0.1,
                    (double)pos.getZ() + random.nextDouble(),
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    @Override
    protected boolean canSurvive(BlockState p_304413_, LevelReader p_304885_, BlockPos p_304808_) {
        return canSupportCenter(p_304885_, p_304808_.below(), Direction.UP);
    }
}
