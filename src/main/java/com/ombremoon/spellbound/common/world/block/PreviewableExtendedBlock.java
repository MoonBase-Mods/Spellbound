package com.ombremoon.spellbound.common.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public interface PreviewableExtendedBlock extends ExtendedBlock {
    /**May save performance and fix translucency glitches if your preview only uses a block entity render
     * usually it's better to override <BlockBehaviour.getRenderShape()> instead*/
    default boolean skipJsonRendering() {
        return false;
    }

    default BlockState getDefaultStateForPreviews(Direction direction) {
        BlockState blockState = getBlock().defaultBlockState().setValue(AbstractExtendedBlock.CENTER, true);

        if (getDirectionProperty() == null) return blockState;
        return blockState.trySetValue(getDirectionProperty(), direction);
    };

    default List<Pair<BlockPos, BlockState>> getPreviewStates(BlockPos posOriginal, BlockState stateOriginal){
        List<Pair<BlockPos, BlockState>> list = new ArrayList<>();

        fullBlockShape(posOriginal, stateOriginal).forEach(posNew -> {

            posNew = posNew.immutable();
            BlockState stateNew = stateOriginal.setValue(AbstractExtendedBlock.CENTER, posOriginal.equals(posNew));
            if (getStateFromOffset() != null) stateNew = getStateFromOffset().apply(stateNew, posNew.subtract(posOriginal));

            list.add(new Pair<>(posNew, stateNew));
        });

        return list;
    }
}
