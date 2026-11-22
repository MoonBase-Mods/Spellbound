package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class ThievesGambitSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<ThievesGambitSpell> createThievesGambitBuilder() {
        return createSimpleSpellBuilder(ThievesGambitSpell.class);
    }

    public ThievesGambitSpell() {
        super(SBSpells.THIEVES_GAMBIT.get(), createThievesGambitBuilder());
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
