package com.ombremoon.spellbound.common.world.spell.ruin.ice;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class FrostBombSpell extends AnimatedSpell {
    public static Builder<FrostBombSpell> createFrostBombBuilder() {
        return createSimpleSpellBuilder(FrostBombSpell.class);
    }

    public FrostBombSpell() {
        super(SBSpells.FROST_BOMB.get(), createFrostBombBuilder());
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
