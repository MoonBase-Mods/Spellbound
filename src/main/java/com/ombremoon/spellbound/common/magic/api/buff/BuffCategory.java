package com.ombremoon.spellbound.common.magic.api.buff;

import net.minecraft.util.StringRepresentable;

public enum BuffCategory implements StringRepresentable {
    BENEFICIAL("beneficial"),
    NEUTRAL("neutral"),
    HARMFUL("harmful");

    public static final StringRepresentableCodec<BuffCategory> CODEC = StringRepresentable.fromEnum(BuffCategory::values);
    private final String name;

    BuffCategory(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
