package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class TransmuteOreSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<TransmuteOreSpell> createTransmuteOreBuilder() {
        return createSimpleSpellBuilder(TransmuteOreSpell.class);
    }

    public TransmuteOreSpell() {
        super(SBSpells.TRANSMUTE_ORE.get(), createTransmuteOreBuilder());
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
