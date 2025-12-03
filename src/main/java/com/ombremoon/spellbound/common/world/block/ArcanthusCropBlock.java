package com.ombremoon.spellbound.common.world.block;

import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ArcanthusCropBlock extends CropBlock {

    public ArcanthusCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(SBTags.Blocks.ARCANTHUS_GROWTH_BLOCKS);
    }
}
