package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class SummonTrophySpell extends SummonSpell {
    public static Builder<SummonTrophySpell> createSummonTrophyBuilder() {
        return createSummonBuilder(SummonTrophySpell.class);
    }

    public SummonTrophySpell() {
        super(SBSpells.SUMMON_TROPHY.get(), createSummonTrophyBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
