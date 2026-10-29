package com.ombremoon.spellbound.common.magic.api.buff;

import net.minecraft.nbt.CompoundTag;

public class MutableSpellModifier {
    private final SpellModifier spellModifier;
    private int currentStacks;

    public MutableSpellModifier(SpellModifier spellModifier) {
        this.spellModifier = spellModifier;
    }

    public float getModifiedAmount() {
        return this.spellModifier.modifier() + this.spellModifier.stackMultiplier() * this.currentStacks;
    }

    public int getCurrentStacks() {
        return this.currentStacks;
    }

    public void incrementStacks(int stacks) {
        if (stacks < this.spellModifier.maxStacks() && this.spellModifier.isStackable()) {
            this.currentStacks = stacks + 1;
        } else {
            this.currentStacks = this.spellModifier.maxStacks();
        }
    }

    public SpellModifier getSpellModifier() {
        return this.spellModifier;
    }

    public CompoundTag serializeModifier() {
        CompoundTag tag = new CompoundTag();
        tag.put("SpellModifier", this.spellModifier.toNBT());
        tag.putInt("CurrentStacks", this.currentStacks);
        return tag;
    }

    public static MutableSpellModifier deserializeModifier(CompoundTag tag) {
        SpellModifier spellModifier = SpellModifier.fromNBT(tag.getCompound("SpellModifier"));
        int currentStacks = tag.getInt("CurrentStacks");
        MutableSpellModifier modifier = new MutableSpellModifier(spellModifier);
        modifier.currentStacks = currentStacks;
        return modifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof MutableSpellModifier spellModifier)) {
            return false;
        } else {
            return this.spellModifier.equals(spellModifier.spellModifier);
        }
    }

    @Override
    public String toString() {
        return "SpellModifier: [" + this.spellModifier + ", Stacks: " + this.currentStacks + "]";
    }
}
