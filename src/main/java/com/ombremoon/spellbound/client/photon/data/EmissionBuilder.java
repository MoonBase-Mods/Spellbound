package com.ombremoon.spellbound.client.photon.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EmissionBuilder extends ConfigDataBuilder {

    public static EmissionBuilder of() {
        return new EmissionBuilder();
    }

    public EmissionBuilder emissionRate(NumberFunctionBuilder emissionRate) {
        this.configData.put("emissionRate", emissionRate.build());
        return this;
    }

    public EmissionBuilder distanceRate(NumberFunctionBuilder distanceRate) {
        this.configData.put("distanceRate", distanceRate.build());
        return this;
    }

    public EmissionBuilder emissionMode(Mode emissionMode) {
        this.configData.putString("emissionMode", emissionMode.name());
        return this;
    }

    public EmissionBuilder bursts(BurstBuilder... bursts) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (BurstBuilder burst : bursts) {
            listTag.add(burst.build());
        }

        tag.put("payload", listTag);
        tag.putInt("uid", listTag.size());
        this.configData.put("bursts", tag);
        return this;
    }

    public enum Mode {
        Exacting,
        Random
    }
}
