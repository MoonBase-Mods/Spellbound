package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;
import net.minecraft.world.Difficulty;

public class SummonUndeadSpell extends SummonSpell implements ChargeableSpell {
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
    public void registerSkillTooltips() {

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

    @Override
    public int maxCharges() {
        return 0;
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return false;
    }
}
