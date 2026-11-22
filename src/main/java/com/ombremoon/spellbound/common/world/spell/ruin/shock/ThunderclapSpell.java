package com.ombremoon.spellbound.common.world.spell.ruin.shock;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class ThunderclapSpell extends AnimatedSpell {
    public static Builder<ThunderclapSpell> createThunderclapBuilder() {
        return createSimpleSpellBuilder(ThunderclapSpell.class);
    }

    public ThunderclapSpell() {
        super(SBSpells.THUNDERCLAP.get(), createThunderclapBuilder());
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
