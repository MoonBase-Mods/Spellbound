package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class CreateObjectSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<CreateObjectSpell> createCreateObjectBuilder() {
        return createSimpleSpellBuilder(CreateObjectSpell.class);
    }

    public CreateObjectSpell() {
        super(SBSpells.CREATE_OBJECT.get(), createCreateObjectBuilder());
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
