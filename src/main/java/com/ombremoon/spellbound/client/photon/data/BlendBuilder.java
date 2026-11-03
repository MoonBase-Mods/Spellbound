package com.ombremoon.spellbound.client.photon.data;

import com.lowdragmc.photon.client.gameobject.emitter.data.material.BlendMode;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class BlendBuilder extends ConfigDataBuilder {

    public static BlendBuilder of() {
        return new BlendBuilder();
    }

    public BlendBuilder enableBlend(boolean enableBlend) {
        this.configData.putBoolean("enableBlend", enableBlend);
        return this;
    }

    public BlendBuilder srcColorFactor(SourceFactor srcColorFactor) {
        this.configData.putString("srcColorFactor", srcColorFactor.name());
        return this;
    }

    public BlendBuilder dstColorFactor(DestFactor dstColorFactor) {
        this.configData.putString("dstColorFactor", dstColorFactor.name());
        return this;
    }

    public BlendBuilder srcAlphaFactor(SourceFactor srcAlphaFactor) {
        this.configData.putString("srcAlphaFactor", srcAlphaFactor.name());
        return this;
    }

    public BlendBuilder dstAlphaFactor(DestFactor dstAlphaFactor) {
        this.configData.putString("dstAlphaFactor", dstAlphaFactor.name());
        return this;
    }

    public BlendBuilder blendFunc(BlendMode.BlendFuc blendFunc) {
        this.configData.putString("blendFunc", blendFunc.name());
        return this;
    }

    public enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        SourceFactor(int value) {
            this.value = value;
        }
    }

    public enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        DestFactor(int value) {
            this.value = value;
        }
    }
}
