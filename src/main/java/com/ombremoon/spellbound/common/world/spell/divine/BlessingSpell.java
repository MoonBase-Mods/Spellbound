package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BlessingSpell extends AnimatedSpell {
    public static Builder<BlessingSpell> createBlessingBuilder() {
        return createSimpleSpellBuilder(BlessingSpell.class)
                .duration(100)
                .manaCost(35)
                .castCondition((context, spell) -> {
                    Entity entity = context.getTarget();
                    return entity instanceof LivingEntity;
                });
    }
    private boolean isOverflowing;

    public BlessingSpell() {
        super(SBSpells.BLESSING.get(), createBlessingBuilder());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (context.isChoice(SBSkills.BLESSING)) {

            } else if (context.isChoice(SBSkills.ARCANE_RESTORATION)) {

            } else if (context.isChoice(SBSkills.SATIATING_BLESSING)) {

            } else if (context.isChoice(SBSkills.AIR_BUBBLE)) {

            } else if (context.isChoice(SBSkills.PURIFYING_WARD)) {

            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.hasSkill(SBSkills.EXTENDED_GRACE) ? 200 : super.getDuration(context);
    }

    @Override
    protected void registerSkillTooltips() {

    }
}
