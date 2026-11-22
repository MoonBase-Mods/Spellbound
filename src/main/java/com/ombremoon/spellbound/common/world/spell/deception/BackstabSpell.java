package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class BackstabSpell extends AnimatedSpell {
    public static Builder<BackstabSpell> createBackstabSpellBuilder() {
        return createSimpleSpellBuilder(BackstabSpell.class);
    }

    public BackstabSpell() {
        super(SBSpells.BACKSTAB.get(), createBackstabSpellBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {

    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
