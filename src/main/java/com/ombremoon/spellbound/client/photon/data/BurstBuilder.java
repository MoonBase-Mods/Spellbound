package com.ombremoon.spellbound.client.photon.data;

import net.minecraft.util.Mth;

public class BurstBuilder extends ConfigDataBuilder {

    public static BurstBuilder of() {
        return new BurstBuilder();
    }

    public BurstBuilder time(int time) {
        this.configData.putInt("time", time);
        return this;
    }

    public BurstBuilder count(NumberFunctionBuilder count) {
        this.configData.put("count", count.build());
        return this;
    }

    public BurstBuilder cycles(int cycles) {
        this.configData.putInt("cycles", Math.max(1, cycles));
        return this;
    }

    public BurstBuilder interval(int time) {
        this.configData.putInt("interval", Math.max(1, time));
        return this;
    }

    public BurstBuilder probability(float probability) {
        this.configData.putFloat("probability", Mth.clamp(probability, 0, 1));
        return this;
    }
}
