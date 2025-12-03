/*
package com.ombremoon.spellbound.common.world.spell.ruin.air;

import com.ombremoon.spellbound.common.world.entity.spell.Cyclone;
import com.ombremoon.spellbound.common.init.SBDataTypes;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class CycloneSpell extends AnimatedSpell {
    protected static final SpellDataKey<Integer> CYCLONE = SyncedSpellData.registerDataKey(CycloneSpell.class, SBDataTypes.INT.get());

    private static Builder<CycloneSpell> createCycloneBuilder() {
        return createSimpleSpellBuilder(CycloneSpell.class)
                .mastery(SpellMastery.MASTER)
                .duration(600)
                .manaCost(95)
                .castCondition((context, cycloneSpell) -> cycloneSpell.hasValidSpawnPos(100));
    }

    public CycloneSpell() {
        super(SBSpells.CYCLONE.get(), createCycloneBuilder());
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        builder.define(CYCLONE, 0);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.setCyclone(summonEntity(context, SBEntities.CYCLONE.get()).getId());

            if (context.getSkills().hasSkill(SBSkills.HAILSTORM.value())) {
//                HailstormSavedData data = ((HailstormSavedData) HailstormSavedData.get(level));
//                data.toggleHailing((ServerLevel) level, this.getDuration());
            }
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide) {
        Cyclone cyclone = this.getCyclone(context);
            if (cyclone == null)
                endSpell();
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            Cyclone cyclone = this.getCyclone(context);
            if (cyclone != null) {
                cyclone.setEndTick(20);
                if (cyclone.getControllingPassenger() instanceof LivingEntity livingEntity)
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * cyclone.getStacks()));
            }
        }
    }

    private void setCyclone(int cyclone) {
        this.spellData.set(CYCLONE, cyclone);
    }

    private Cyclone getCyclone(SpellContext context) {
        Entity entity = context.getLevel().getEntity(this.spellData.get(CYCLONE));
        return entity instanceof Cyclone cyclone ? cyclone : null;
    }
}
*/
