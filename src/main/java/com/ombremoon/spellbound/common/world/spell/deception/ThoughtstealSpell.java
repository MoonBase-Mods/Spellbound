package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class ThoughtstealSpell extends AnimatedSpell {
    public static Builder<ThoughtstealSpell> createThoughtstealBuilder() {
        return createSimpleSpellBuilder(ThoughtstealSpell.class);
    }

    public ThoughtstealSpell() {
        super(SBSpells.THOUGHTSTEAL.get(), createThoughtstealBuilder());
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
