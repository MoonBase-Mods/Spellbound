package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class JebsLambSpell extends AnimatedSpell {
    public static Builder<JebsLambSpell> createJebsLambBuilder() {
        return createSimpleSpellBuilder(JebsLambSpell.class);
    }

    public JebsLambSpell() {
        super(SBSpells.JEBS_LAMB.get(), createJebsLambBuilder());
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
