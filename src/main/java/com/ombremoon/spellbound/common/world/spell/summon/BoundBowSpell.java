package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class BoundBowSpell extends SummonSpell implements RadialSpell {
    public static Builder<BoundBowSpell> createBoundBowSpellBuilder() {
        return createSummonBuilder(BoundBowSpell.class);
    }

    public BoundBowSpell() {
        super(SBSpells.BOUND_BOW.get(), createBoundBowSpellBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }
}
