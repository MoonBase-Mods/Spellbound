package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class SapLifeSpell extends AnimatedSpell {
    public static Builder<SapLifeSpell> createSapLifeBuilder() {
        return createSimpleSpellBuilder(SapLifeSpell.class);
    }

    public SapLifeSpell() {
        super(SBSpells.SAP_LIFE.get(), createSapLifeBuilder());
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
