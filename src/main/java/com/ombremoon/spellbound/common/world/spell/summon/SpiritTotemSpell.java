/*
package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.world.entity.living.TotemSpiritEntity;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;

public class SpiritTotemSpell extends SummonSpell {

    public static Builder<SpiritTotemSpell> createSpiritTotemBuilder() {
        return createSummonBuilder(SpiritTotemSpell.class)
                .mastery(SpellMastery.ADEPT)
                .manaCost(32)
                .duration(600);
    }

    public SpiritTotemSpell() {
        super(SBSpells.SPIRIT_TOTEM.get(), createSpiritTotemBuilder());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);

        if (context.isRecast() && context.getSkills().hasSkill(SBSkills.TWIN_SPIRITS.value())) return;
        addSpiritTotem(context);
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        SpellHandler handler = context.getSpellHandler();
        SkillHolder skills = context.getSkills();

        if (!skills.hasSkill(SBSkills.TWIN_SPIRITS.value())) {
            handler.getActiveSpells(spellType()).getFirst().endSpell();
        } else {
            SpiritTotemSpell twinSpell = (SpiritTotemSpell) handler.getActiveSpells(spellType()).get(1);
            if (handler.getActiveSpells(spellType()).size() > 2) {
                handler.getActiveSpells(spellType()).getFirst().endSpell();
            }

            TotemSpiritEntity entity = (TotemSpiritEntity) context.getLevel().getEntity(twinSpell.getSummons().iterator().next());
            entity.setTwin(true);

            TotemSpiritEntity totem = addSpiritTotem(context);
            totem.setTwin(true);
            if (!entity.isCatForm()) totem.switchForm();
        }
    }

    private TotemSpiritEntity addSpiritTotem(SpellContext context) {
//        Set<TotemSpiritEntity> mobs = addMobs(context, EntityInit.TOTEM_SPIRIT.get(), 1);
//        if (mobs == null || mobs.isEmpty()) {
//            endSpell();
//            //TODO: refund mana
//            return null;
//        }
//
//        TotemSpiritEntity spirit = mobs.iterator().next();
//        spirit.initSpell(context.getSkills(), this);
//        return spirit;
        endSpell();
        return null;
    }
}
*/
