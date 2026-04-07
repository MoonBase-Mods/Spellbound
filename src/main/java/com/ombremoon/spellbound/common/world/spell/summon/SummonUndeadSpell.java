package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.*;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.effect.SBEffectInstance;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SummonUndeadSpell extends SummonSpell implements ChargeableSpell, RadialSpell {
    private static final ResourceLocation SUNKEN_BREATH = CommonClass.customLocation("sunken_breath");
    private static final ResourceLocation SILENT_NIGHT = CommonClass.customLocation("silent_night");
    private static final SpellDataKey<Boolean> CAN_EXPLODE = SyncedSpellData.registerDataKey(SummonUndeadSpell.class, SBDataTypes.BOOLEAN.get());
    private long piglinBonusTick;

    public static Builder<SummonUndeadSpell> createSummonBuilder() {
        return createSummonBuilder(SummonUndeadSpell.class)
                .manaCost(10)
                .duration(2400)
                .isSpecialChoice()
                .additionalCondition((context, summonUndeadSpell) -> !summonUndeadSpell.skipEndOnRecast(context) && context.getLevel().getDifficulty() != Difficulty.PEACEFUL)
                .castAnimation((context, spell) -> new SpellAnimation(shouldExplodeCorpse(context) || spell.hasSpecialChoice(spell, context) ? "instant_cast" : "summon", SpellAnimation.Type.CAST, true))
                .skipEndOnRecast(context -> {
                    LivingEntity caster = context.getCaster();
                    var handler = context.getSpellHandler();
                    if (context.hasSkill(SBSkills.CORPSE_EXPLOSION) && context.getTarget() instanceof LivingEntity livingEntity) {
                        AbstractSpell spell = SpellUtil.getActiveSpell(livingEntity);
                        AbstractSpell spell1 = handler.getCurrentlyCastSpell();
                        if (spell != null && spell.isSpellType(SBSpells.SUMMON_UNDEAD) && spell.isCaster(caster) && spell1 instanceof SummonUndeadSpell summonUndeadSpell && summonUndeadSpell.canExplode()) {
                            spell.heal(caster, livingEntity.getHealth() * spell.potency(0.1F));
                            livingEntity.kill();

                            List<LivingEntity> list = spell1.getAttackableEntities(livingEntity, 2);
                            for (LivingEntity entity : list) {
                                spell.hurt(entity, 2.0F);
                            }

                            return true;
                        }
                    }

                    return false;
                });
    }

    private static boolean shouldExplodeCorpse(SpellContext context) {
        LivingEntity caster = context.getCaster();
        if (context.hasSkill(SBSkills.CORPSE_EXPLOSION) && context.getTarget() instanceof LivingEntity livingEntity && SpellUtil.isSummonOf(livingEntity, caster)) {
            ResourceLocation spell = livingEntity.getData(SBData.SPELL_TYPE);
            return SBSpells.SUMMON_UNDEAD.get().is(spell);
        }

        return false;
    }

    public SummonUndeadSpell() {
        super(SBSpells.SUMMON_UNDEAD.get(), createSummonBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.SUMMON_UNDEAD,
                SkillTooltip.DURATION.tooltip(2400),
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip(),
                SkillTooltip.CAST_SCALES.tooltip()
        );
        this.addSkillDetails(SBSkills.SUMMON_SKELETON,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.SUMMON_DROWNED,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.SUMMON_PHANTOM,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.SUMMON_ZOMBIFIED_PIGLIN,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.CRIMSON_PACT,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ATTACK_DAMAGE, new AttributeModifier(SUNKEN_BREATH, potency(0.15F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
        );
        this.addSkillDetails(SBSkills.HALL_OF_THE_DEAD,
                SkillTooltip.PROC_DURATION.tooltip(100)
        );
        this.addSkillDetails(SBSkills.SILENT_NIGHT,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.SILENCED),
                SkillTooltip.EFFECT_DURATION.tooltip(60)
        );
        this.addSkillDetails(SBSkills.SUNKEN_BREATH,
                SkillTooltip.MOB_EFFECT.tooltip(MobEffects.WATER_BREATHING)
        );
        this.addSkillDetails(SBSkills.CORPSE_EXPLOSION,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, this.getModifiedDamage(2F))),
                SkillTooltip.RADIUS.tooltip(2F),
                SkillTooltip.HEAL_FROM_TARGET.tooltip(potency(10F))
        );
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(CAN_EXPLODE, false);
    }

    @Override
    public int getCastTime(SpellContext context) {
        return shouldExplodeCorpse(context) || this.hasSpecialChoice(this, context) ? 1 : this.maxCharges(context) * 20;
    }

    @Override
    public void onCastStart(SpellContext context) {
        if (shouldExplodeCorpse(context)) {
            this.setExploding(true);
        }

        super.onCastStart(context);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        int charges = this.getCharges() + 1;
        if (!context.getLevel().isClientSide) {
            EntityType<?> undead = EntityType.ZOMBIE;
            if (context.isChoice(SBSkills.SUMMON_ZOMBIFIED_PIGLIN)) {
                undead = EntityType.ZOMBIFIED_PIGLIN;
            } else if (context.isChoice(SBSkills.SUMMON_SKELETON)) {
                undead = EntityType.SKELETON;
            } else if (context.isChoice(SBSkills.SUMMON_PHANTOM)) {
                undead = EntityType.PHANTOM;
            } else if (context.isChoice(SBSkills.SUMMON_DROWNED)) {
                undead = EntityType.DROWNED;
            }

            float radius = 1.5F;
            Vec3 origin = caster.position();
            float yaw = caster.getYRot();
            for (int i = 0; i < charges; i++) {
                Vec3 spawnOffset = this.getSurroundingSpawnPosition(origin, yaw, radius, i, charges);
                this.summonEntity(context, undead, spawnOffset);
            }
            (context.getLevel()).playSound(null, context.getCaster().blockPosition(), SoundEvents.TRIAL_SPAWNER_AMBIENT,
                    SoundSource.PLAYERS, 1F, 0.8F);
        }

        if (context.isChoice(SBSkills.SUMMON_DROWNED) && context.hasSkill(SBSkills.SUNKEN_BREATH)) {
            this.addSkillBuff(
                    caster,
                    SBSkills.SUNKEN_BREATH,
                    SUNKEN_BREATH,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.MOB_EFFECT,
                    new MobEffectInstance(MobEffects.WATER_BREATHING, -1)
            );
        }
    }

    @Override
    public void onMobIncomingHurt(SpellContext context, LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity instanceof ZombifiedPiglin && context.hasSkill(SBSkills.CRIMSON_PACT) && (source.is(DamageTypeTags.IS_FIRE))) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onMobPreDamage(SpellContext context, LivingDamageEvent.Pre event) {
        Entity mob = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        Level level = context.getLevel();
        if (mob instanceof ZombifiedPiglin && context.hasSkill(SBSkills.CRIMSON_PACT)) {
            if (level.getGameTime() < this.piglinBonusTick) {
                event.setNewDamage(event.getOriginalDamage() * potency(1.15F));
            } else if (target.getLastAttacker().is(context.getCaster())) {
                this.piglinBonusTick = level.getGameTime() + 100;
            }
        }
    }

    @Override
    public void onMobPostDamage(SpellContext context, LivingDamageEvent.Post event) {
        Entity mob = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        float amount = event.getNewDamage();
        if (mob instanceof Zombie zombie && context.hasSkill(SBSkills.ROTTEN_SOLDIERS)) {
            this.incrementEffect(zombie, EffectManager.Effect.DISEASE, amount);
        }

        if (mob instanceof Phantom && context.hasSkill(SBSkills.SILENT_NIGHT)) {
            this.addSkillBuff(
                    target,
                    SBSkills.SILENT_NIGHT,
                    SILENT_NIGHT,
                    BuffCategory.HARMFUL,
                    SkillBuff.MOB_EFFECT,
                    new SBEffectInstance(context.getCaster(), SBEffects.SILENCED, 60),
                    60
            );
        }
        super.onMobPostDamage(context, event);
    }

    @Override
    public void onMobRemoved(LivingEntity entity, SpellContext context, @Nullable DamageSource source, Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.KILLED) {
            //Spawn pile of bones
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        LivingEntity caster = context.getCaster();
        this.removeSkillBuff(caster, SBSkills.SUNKEN_BREATH);
    }

    @Override
    public int maxCharges(SpellContext context) {
        int defaultMax = context.getSpellLevel() + 1;
        int currentSummonSize = this.getSummonSize(context);
        return Math.max(0, defaultMax - currentSummonSize);
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return !shouldExplodeCorpse(context);
    }

    public void setExploding(boolean exploding) {
        this.spellData.set(CAN_EXPLODE, exploding);
    }

    public boolean canExplode() {
        return this.spellData.get(CAN_EXPLODE);
    }
}
