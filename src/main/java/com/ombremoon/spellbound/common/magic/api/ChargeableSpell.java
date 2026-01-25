package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.magic.skills.Skill;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public interface ChargeableSpell {

    int maxCharges();

    boolean canCharge(SkillTester skills);

    @FunctionalInterface
    interface SkillTester {
        boolean test(Holder<Skill> skill, @Nullable Holder<Skill> choice);

        default boolean test(Holder<Skill> skill) {
            return this.test(skill, skill);
        }
    }
}
