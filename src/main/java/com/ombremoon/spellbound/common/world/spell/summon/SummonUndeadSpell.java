/*
package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;
import net.minecraft.world.Difficulty;

public class SummonUndeadSpell extends SummonSpell {
    public static Builder<SummonUndeadSpell> createSummonBuilder() {
        return createSummonBuilder(SummonUndeadSpell.class)
                .manaCost(10)
                .duration(180)
                .additionalCondition((context, spells) -> context.getLevel().getDifficulty() != Difficulty.PEACEFUL);
    }

    public SummonUndeadSpell() {
        super(SBSpells.SUMMON_UNDEAD.get(), createSummonBuilder());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        if (!context.getLevel().isClientSide) {
//            var success = summonMobs(context, EntityType.ZOMBIE, 1);
//            if (success == null) {
//                endSpell();
//                context.getSpellHandler().awardMana(this.getManaCost());
//            }
        }
    }
}
*/
