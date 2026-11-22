package com.ombremoon.spellbound.common.world.spell.ruin.shock;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;

public class ElectricOrbSpell extends AnimatedSpell {
    public static Builder<ElectricOrbSpell> createElectricOrbBuilder() {
        return createSimpleSpellBuilder(ElectricOrbSpell.class);
    }

    public ElectricOrbSpell() {
        super(SBSpells.ELECTRIC_ORB.get(), createElectricOrbBuilder());
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
