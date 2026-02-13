package com.ombremoon.spellbound.common.world;

import com.ombremoon.spellbound.common.world.dimension.DimensionCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

public interface StructureHolderData {

    void setStructureBounds(@Nullable BoundingBox bounds);

    @Nullable BoundingBox getStructureBounds();

    default void destroyDimension(ServerLevel level) {
        DimensionCreator.get().markDimensionForUnregistration(level.getServer(), level.dimension());
    }

    default @Nullable BlockPos getStructureCenter() {
        BoundingBox bounds = getStructureBounds();
        return bounds != null ? bounds.getCenter() : null;
    }
}
