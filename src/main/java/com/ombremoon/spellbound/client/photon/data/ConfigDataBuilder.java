package com.ombremoon.spellbound.client.photon.data;

import net.minecraft.nbt.CompoundTag;

public abstract class ConfigDataBuilder {
    protected final CompoundTag configData = new CompoundTag();

    public CompoundTag build() {
        return this.configData;
    }
}
