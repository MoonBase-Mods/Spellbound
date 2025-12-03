package com.ombremoon.spellbound.common.world.multiblock;

import net.minecraft.resources.ResourceLocation;

public interface MultiblockOutput {
    void accept(ResourceLocation location, Multiblock multiblock);
}
