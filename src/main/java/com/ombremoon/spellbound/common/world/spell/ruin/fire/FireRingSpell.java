package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class FireRingSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<FireRingSpell> createFireRingBuilder() {
        return createSimpleSpellBuilder(FireRingSpell.class);
    }

    public FireRingSpell() {
        super(SBSpells.FIRE_RING.get(), createFireRingBuilder());
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
