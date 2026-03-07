package com.ombremoon.spellbound.common.world.spell.ruin.shock;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.spell.StormstrikeBolt;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class StormstrikeSpell extends AnimatedSpell {
    public static Builder<StormstrikeSpell> createStormstrikeBuilder() {
        return createSimpleSpellBuilder(StormstrikeSpell.class)
                .manaCost(10)
                .baseDamage(2)
                .castTime(15);
    }

    public StormstrikeSpell() {
        super(SBSpells.STORMSTRIKE.get(), createStormstrikeBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.STORMSTRIKE,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.SHOCK, this.getModifiedDamage())),
                SkillTooltip.DURATION.tooltip(60),
                SkillTooltip.PROC_DURATION.tooltip(20)
        );
        this.addSkillDetails(SBSkills.STATIC_SHOCK, SkillTooltip.RADIUS.tooltip(3F));
        this.addSkillDetails(SBSkills.ELECTRIFY,
                SkillTooltip.TARGET_SHOCK_RESIST.tooltip(-30F),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.SHOCK_FACTOR, SkillTooltip.MANA_TO_DAMAGE.tooltip(1F));
        this.addSkillDetails(SBSkills.PURGE, SkillTooltip.MANA_TO_DAMAGE.tooltip(10F));
        this.addSkillDetails(SBSkills.REFRACTION,
                SkillTooltip.MANA.tooltip(potency(5)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.PULSATION,
                SkillTooltip.PROC_CHANCE.tooltip(10F),
                SkillTooltip.EFFECT_DURATION.tooltip(20),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.STORM_SHARD, SkillTooltip.COOLDOWN.tooltip(24000));
        this.addSkillDetails(SBSkills.CHARGED_ATMOSPHERE,
                SkillTooltip.MODIFY_MANA_COST.tooltip(-25F),
                SkillTooltip.EFFECT_DURATION.tooltip(160)
        );
        this.addSkillDetails(SBSkills.DISARM,
                SkillTooltip.PROC_CHANCE.tooltip(20F),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.SUPERCHARGE,
                SkillTooltip.POTENCY.tooltip(50F),
                SkillTooltip.EFFECT_DURATION.tooltip(200)
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            playCastSound(level, context);
            this.shootProjectile(context, SBEntities.STORMSTRIKE_BOLT.get(), 2.5F, 1.0F);
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    public void playCastSound(Level level, SpellContext context){
        float volume = 0.2F + level.random.nextFloat() * 0.2F;
        float pitch = 0.7F + level.random.nextFloat() * 0.3F;
        level.playSound(null, context.getCaster().blockPosition(), SpellboundSounds.STORMSTRIKE_USE.get(),
                SoundSource.PLAYERS, volume, pitch);
        level.playSound(null, context.getCaster().blockPosition(), SpellboundSounds.INTERFERENCE_ZAP.get(),
                SoundSource.PLAYERS, volume*0.6F, pitch);
    }

    @Override
    public void onEntityTick(ISpellEntity<?> spellEntity, SpellContext context) {
        if (spellEntity instanceof StormstrikeBolt bolt) {
            Level level = context.getLevel();
            LivingEntity caster = context.getCaster();
            var handler = context.getSpellHandler();
            if (bolt.isInWaterOrBubble()) {
                hurtSurroundingEnemies(level, caster, handler, bolt, 5, true);
                bolt.discard();
            }
        }
    }

    @Override
    public void onProjectileHitEntity(ISpellEntity<?> spellEntity, SpellContext context, EntityHitResult result) {
        if (spellEntity instanceof StormstrikeBolt bolt) {
            LivingEntity caster = context.getCaster();
            Entity entity = result.getEntity();

            if (entity.is(caster)) return;

            if (entity instanceof LivingEntity livingEntity) {
                context.getSpellHandler().applyStormStrike(livingEntity, 60);
                bolt.discard();
            }
        }
    }

    @Override
    public void onProjectileHitBlock(ISpellEntity<?> spellEntity, SpellContext context, BlockHitResult result) {
        if (spellEntity instanceof StormstrikeBolt bolt) {
            Level level = context.getLevel();
            LivingEntity caster = context.getCaster();
            var handler = context.getSpellHandler();
            if (context.hasSkill(SBSkills.STATIC_SHOCK))
                hurtSurroundingEnemies(level, caster, handler, bolt, 3, false);

            bolt.discard();
        }
    }

    private void hurtSurroundingEnemies(Level level, LivingEntity caster, SpellHandler handler, StormstrikeBolt bolt, int range, boolean inWater) {
        var entities = level.getEntitiesOfClass(LivingEntity.class, bolt.getBoundingBox().inflate(range), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity target && !target.is(caster) && !target.isAlliedTo(caster) && !checkForCounterMagic(target)) {
                if (inWater && target.isInWaterOrBubble()) {
                    handler.applyStormStrike(target, 60);
                }

                if (inWater && !target.isInWaterOrBubble()) return;

                this.hurt(target, 5.0F);
            }
        }
    }
}
