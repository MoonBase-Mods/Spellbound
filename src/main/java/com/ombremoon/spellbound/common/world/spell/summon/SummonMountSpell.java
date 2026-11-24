package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class SummonMountSpell extends SummonSpell {
    public static Builder<SummonMountSpell> createSummonMountBuilder() {
        return createSummonBuilder(SummonMountSpell.class);
    }

    public SummonMountSpell() {
        super(SBSpells.SUMMON_MOUNT.get(), createSummonMountBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
