package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class FertilizeSpell extends ChanneledSpell implements RadialSpell {
    public static Builder<FertilizeSpell> createFertilizeBuilder() {
        return createChannelledSpellBuilder(FertilizeSpell.class);
    }

    public FertilizeSpell() {
        super(SBSpells.FERTILIZE.get(), createFertilizeBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
