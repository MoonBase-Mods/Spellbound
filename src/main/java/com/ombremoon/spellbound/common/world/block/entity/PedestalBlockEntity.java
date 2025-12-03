package com.ombremoon.spellbound.common.world.block.entity;

import com.ombremoon.spellbound.common.world.multiblock.TransfigurationMultiblockPart;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalBlockEntity extends TransfigurationMultiblockPart {
    public PedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public PedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBBlockEntities.PEDESTAL.get(), pos, blockState);
    }
}
