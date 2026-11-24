package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class LocateObjectSpell extends ChanneledSpell implements RadialSpell {
    public static Builder<LocateObjectSpell> createLocateObjectBuilder() {
        return createChannelledSpellBuilder(LocateObjectSpell.class);
    }

    public LocateObjectSpell() {
        super(SBSpells.LOCATE_OBJECT.get(), createLocateObjectBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
