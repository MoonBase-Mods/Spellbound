package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class MagmaHandsSpell extends AnimatedSpell {
    public static Builder<MagmaHandsSpell> createMagmaHandsBuilder() {
        return createSimpleSpellBuilder(MagmaHandsSpell.class);
    }

    public MagmaHandsSpell() {
        super(SBSpells.MAGMA_HANDS.get(), createMagmaHandsBuilder());
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
