package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class PocketStorageSpell extends AnimatedSpell implements RadialSpell {
    public static Builder<PocketStorageSpell> createPocketStorageBuilder() {
        return createSimpleSpellBuilder(PocketStorageSpell.class);
    }

    public PocketStorageSpell() {
        super(SBSpells.POCKET_STORAGE.get(), createPocketStorageBuilder());
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
