package com.ombremoon.spellbound.common.world;

import net.minecraft.network.chat.Component;

public enum DamageTranslation {
    MAGIC(Component.translatable("spellbound.sb_generic")),
    FIRE(Component.translatable("spellbound.ruin_fire")),
    FROST(Component.translatable("spellbound.ruin_frost")),
    SHOCK(Component.translatable("spellbound.ruin_shock"));

    private final Component name;

    DamageTranslation(Component name) {
        this.name = name;
    }

    public Component getName() {
        return this.name;
    }
}
