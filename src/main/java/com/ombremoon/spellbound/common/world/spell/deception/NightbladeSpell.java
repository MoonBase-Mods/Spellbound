package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.ImbuementSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;

public class NightbladeSpell extends ImbuementSpell {
    public static Builder<NightbladeSpell> createNightbladeBuilder() {
        return createImbuementSpellBuilder(NightbladeSpell.class)
                .duration(200);
    }

    public NightbladeSpell() {
        super(SBSpells.NIGHTBLADE.get(), createNightbladeBuilder());
    }

    @Override
    protected void onUseImbuement(SpellContext context) {

    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
