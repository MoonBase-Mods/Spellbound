package com.ombremoon.spellbound.common.magic.api.combat;

import com.ombremoon.spellbound.common.magic.api.SpellAnimation;

import java.util.List;

public record AttackMoveset(List<SpellAnimation> animations) {

    public boolean isEmpty() {
        return this.animations.isEmpty();
    }

    public int size() {
        return this.animations.size();
    }

    public SpellAnimation getAnimation(int index) {
        return this.animations.get(index);
    }
}
