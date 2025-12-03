package com.ombremoon.spellbound.common.world.block.entity;

import com.ombremoon.spellbound.common.world.multiblock.TransfigurationMultiblockPart;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RuneBlockEntity extends TransfigurationMultiblockPart {

    public RuneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public RuneBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBBlockEntities.RUNE.get(), pos, blockState);
    }
}
