package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class ArcaneArsenalSpell extends SummonSpell {
    public static Builder<ArcaneArsenalSpell> createArcaneArsenalBuilder() {
        return createSummonBuilder(ArcaneArsenalSpell.class);
    }

    public ArcaneArsenalSpell() {
        super(SBSpells.ARCANE_ARSENAL.get(), createArcaneArsenalBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
