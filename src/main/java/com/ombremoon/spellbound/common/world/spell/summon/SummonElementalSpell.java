package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class SummonElementalSpell extends SummonSpell {
    public static Builder<SummonElementalSpell> createSummonElementalBuilder() {
        return createSummonBuilder(SummonElementalSpell.class);
    }

    public SummonElementalSpell() {
        super(SBSpells.SUMMON_ELEMENTAL.get(), createSummonElementalBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
