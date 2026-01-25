package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class SummonWolfPackSpell extends SummonSpell {
    public SummonWolfPackSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, builder);
    }

    @Override
    protected void registerSkillTooltips() {

    }
}
