package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class SacredGroundSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<SacredGroundSpell> createSacredGroundBuilder() {
        return createSimpleSpellBuilder(SacredGroundSpell.class);
    }

    public SacredGroundSpell() {
        super(SBSpells.SACRED_GROUND.get(), createSacredGroundBuilder());
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
