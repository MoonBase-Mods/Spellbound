package com.ombremoon.spellbound.common.world.item;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WhistleMaterial implements StringRepresentable {
    WOOD("wood"),
    STONE("stone"),
    BONE("bone"),
    BAMBOO("bamboo");

    public static final Codec<WhistleMaterial> CODEC = StringRepresentable.fromEnum(WhistleMaterial::values);
    private final String name;

    WhistleMaterial(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}