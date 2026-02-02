package com.ombremoon.spellbound.common.magic;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public enum SpellPath implements StringRepresentable {
    RUIN(0x7C0A02, false, "ruin", null, 2),
    TRANSFIGURATION(0x32CD32, false, "transfiguration", null, 4),
    SUMMONS(0x055C9D, false, "summons", null, 3),
    DIVINE(0xD4AF37, false, "divine", null, 1),
    DARK_DIVINE(0xD4AF37, true, "dark_divine", EffectManager.Effect.DISEASE, 1),
    DECEPTION(0x541675, false, "deception", null, 0),
    FIRE(0xD73502, true, "fire", EffectManager.Effect.FIRE, 2),
    FROST(0x4F9CC8, true, "frost", EffectManager.Effect.FROST, 2),
    SHOCK(0x9543C9, true, "shock", EffectManager.Effect.SHOCK, 2);

    public static final Codec<SpellPath> CODEC = StringRepresentable.fromEnum(SpellPath::values);
    private final int color;
    private final boolean isSubPath;
    private final String name;
    @Nullable
    private final EffectManager.Effect effect;
    private final int toastOrdinal;

    SpellPath(int color, boolean isSubPath, String name, @Nullable EffectManager.Effect effect, int toastOrdinal) {
        this.color = color;
        this.isSubPath = isSubPath;
        this.name = name;
        this.effect = effect;
        this.toastOrdinal = toastOrdinal;
    }

    public int getToastOrdinal() {
        return toastOrdinal;
    }

    public int getColor() {
        return this.color;
    }

    public boolean isSubPath() {
        return this.isSubPath;
    }

    public boolean isDivine() {
        return this == DIVINE || this == DARK_DIVINE;
    }

    public @Nullable EffectManager.Effect getEffect() {
        return this.effect;
    }

    public static SpellPath getPathById(int ordinal) {
        return SpellPath.values()[ordinal];
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
