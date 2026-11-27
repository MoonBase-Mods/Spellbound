package com.ombremoon.spellbound.common.magic.api.combat;

public enum AnimationRule {
    SPELL_CAST(true),
    CAN_MOVE(true),
    CAN_ATTACK(true),
    CAN_TURN(true),
    INPUT_LOCK(false),
    IS_ATTACKING(false);

    private final boolean defaultRule;

    AnimationRule(boolean defaultRule) {
        this.defaultRule = defaultRule;
    }

    public boolean defaultRule() {
        return this.defaultRule;
    }
}
