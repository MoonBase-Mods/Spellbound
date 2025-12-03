package com.ombremoon.spellbound.common.world.multiblock;

import com.ombremoon.spellbound.util.Loggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface MultiblockPart extends Loggable {

    Multiblock getMultiblock();

    MultiblockIndex getIndex();

    void assign(Multiblock multiblock, MultiblockIndex index, Direction facing);

    Direction getPartDirection();

    boolean isAssigned();

    default void onCleared(Level level, BlockPos blockPos) {

    }

    void save(CompoundTag tag);

    void load(CompoundTag tag);
}
