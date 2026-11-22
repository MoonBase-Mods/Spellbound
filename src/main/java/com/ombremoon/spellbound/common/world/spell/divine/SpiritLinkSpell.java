package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;

public class SpiritLinkSpell extends ChanneledSpell implements RadialSpell {
    public static Builder<SpiritLinkSpell> createSpritLinkBuilder() {
        return createChannelledSpellBuilder(SpiritLinkSpell.class);
    }

    public SpiritLinkSpell() {
        super(SBSpells.SPIRIT_LINK.get(), createSpritLinkBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
