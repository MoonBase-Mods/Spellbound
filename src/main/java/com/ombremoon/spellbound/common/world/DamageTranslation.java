package com.ombremoon.spellbound.common.world;

import net.minecraft.network.chat.Component;

public enum DamageTranslation {
    DEFAULT(Component.translatable("spellbound.default_damage")),
    MAGIC(Component.translatable("spellbound.sb_generic")),
    PHYSICAL(Component.translatable("spellbound.physical_damage")),
    FIRE(Component.translatable("spellbound.ruin_fire")),
    FROST(Component.translatable("spellbound.ruin_frost")),
    SHOCK(Component.translatable("spellbound.ruin_shock")),
    HOLY(Component.translatable("spellbound.holy")),;

    private final Component name;

    DamageTranslation(Component name) {
        this.name = name;
    }

    public Component getName() {
        return this.name;
    }
}
