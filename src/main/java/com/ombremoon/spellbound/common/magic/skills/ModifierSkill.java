package com.ombremoon.spellbound.common.magic.skills;

import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ModifierSkill extends Skill {
    private final ObjectArrayList<SpellModifier> modifiers = new ObjectArrayList<>();

    public ModifierSkill(int xPos, int yPos, @Nullable HolderSet<Skill> prerequisites, SpellModifier... spellModifiers) {
        super(xPos, yPos, prerequisites);
        this.modifiers.addAll(Arrays.stream(spellModifiers).toList());
    }

    public ObjectArrayList<SpellModifier> getModifiers() {
        return this.modifiers;
    }
}
