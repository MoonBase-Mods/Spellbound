package com.ombremoon.spellbound.common.world.multiblock;

import net.minecraft.resources.ResourceLocation;

public interface MultiblockRegistration {

    void build(MultiblockOutput output, ResourceLocation location);
}
