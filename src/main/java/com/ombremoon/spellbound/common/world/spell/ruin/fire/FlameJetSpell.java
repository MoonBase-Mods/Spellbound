package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;

public class FlameJetSpell extends AnimatedSpell implements ChargeableSpell {
    public static Builder<FlameJetSpell> createFlameJetBuilder() {
        return createSimpleSpellBuilder(FlameJetSpell.class)
                .duration(20)
                .manaCost(35)
                .castTime(20)
                .fullRecast();
    }

    public FlameJetSpell() {
        super(SBSpells.FLAME_JET.get(), createFlameJetBuilder());
    }

    @Override
    protected void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        log(this.getCharges());
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    public int maxCharges() {
        return 3;
    }

    @Override
    public boolean canCharge(SkillTester skills) {
        return /*skills.test(SBSkills.TURBO_CHARGE);*/true;
    }

    @Override
    public boolean isStationaryCast(SpellContext context) {
        return false;
    }
}
