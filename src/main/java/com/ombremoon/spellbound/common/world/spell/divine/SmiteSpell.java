package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.magic.api.ImbuementSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;

public class SmiteSpell extends ImbuementSpell {
    public SmiteSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, builder);
    }

    @Override
    protected void registerSkillTooltips() {

    }
}
