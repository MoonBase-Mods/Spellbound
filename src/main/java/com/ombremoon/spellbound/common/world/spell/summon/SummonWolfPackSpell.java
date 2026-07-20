package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class SummonWolfPackSpell extends SummonSpell implements RadialSpell {
    public static Builder<SummonWolfPackSpell> createSummonWolfPackSpellBuilder() {
        return createSummonBuilder(SummonWolfPackSpell.class);
    }

    public SummonWolfPackSpell() {
        super(SBSpells.SUMMON_WOLF_PACK.get(), createSummonWolfPackSpellBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
