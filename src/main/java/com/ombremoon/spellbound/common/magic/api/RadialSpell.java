package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.skills.SkillProvider;

import java.util.List;

public interface RadialSpell {

    default SkillProvider getChoice() {
        return ((AbstractSpell) this).choice;
    }

    default void setChoice(SkillProvider choice) {
        ((AbstractSpell) this).choice = choice;
    }

    default boolean isMainChoice(SpellContext context) {
        AbstractSpell spell = (AbstractSpell) this;
        return this.getChoice() == spell.spellType().getRootSkill();
    }

    default boolean disableChoiceOnRecast(SpellContext context, AbstractSpell spell) {
        var handler = context.getSpellHandler();
        if (context.isRecast()) {
            List<AbstractSpell> spells = handler.getActiveSpells(spell.spellType());
            for (AbstractSpell abstractSpell : spells) {
                if (abstractSpell.isChoice(this.getChoice())) {
                    abstractSpell.endSpell();
                    return true;
                }
            }
        }

        return false;
    }
}
