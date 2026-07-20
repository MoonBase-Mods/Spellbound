package com.ombremoon.spellbound.common.world.spell.ruin.ice;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class IceBoltSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<IceBoltSpell> createIceBoltBuilder() {
        return createSimpleSpellBuilder(IceBoltSpell.class);
    }

    public IceBoltSpell() {
        super(SBSpells.ICE_BOLT.get(), createIceBoltBuilder());
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
