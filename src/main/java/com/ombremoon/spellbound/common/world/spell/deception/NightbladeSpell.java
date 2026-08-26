package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.ImbuementSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import com.ombremoon.spellbound.common.world.effect.SBEffectInstance;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class NightbladeSpell extends ImbuementSpell {
    private static final ResourceLocation NIGHTBLADE_PRE = CommonClass.customLocation("nightblade_pre");
    private static final ResourceLocation NIGHTBLADE_POST = CommonClass.customLocation("nightblade_post");
    private static final ResourceLocation NUMBING_POISON = CommonClass.customLocation("numbing_poison");
    private static final ResourceLocation EVASIVE_STANCE = CommonClass.customLocation("evasive_stance");
    private static final ResourceLocation MUFFLE = CommonClass.customLocation("muffle");
    private static final ResourceLocation UMBRAL_SIGHT = CommonClass.customLocation("umbral_sight");
    public static Builder<NightbladeSpell> createNightbladeBuilder() {
        return createImbuementSpellBuilder(NightbladeSpell.class)
                .requiresCharges()
                .duration(200);
    }
    private float bonusDamage;

    public NightbladeSpell() {
        super(SBSpells.NIGHTBLADE.get(), createNightbladeBuilder());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.addEventBuff(
                    caster,
                    SBSkills.NIGHTBLADE,
                    BuffCategory.BENEFICIAL,
                    SpellEventListener.Events.DEALT_DAMAGE_PRE,
                    NIGHTBLADE_PRE,
                    pre -> {
                        if (pre.getTarget() instanceof LivingEntity target) {
                            DamageSource damageSource = pre.getSource();
                            if (damageSource instanceof SpellDamageSource spellSource) {
                                boolean isBehind = this.isAttackFromBehind(target, spellSource);
                                if (isBehind && context.hasSkill(SBSkills.UNEXPECTED)) {
                                    spellSource.addDamageInstance(DamageInstance.of(SBDamageTypes.SB_GENERIC, this.bonusDamage));
                                }
                            }

                            if (context.hasSkill(SBSkills.THROAT_SLIT)) {
                                pre.setNewDamage(pre.getOriginalDamage() * 1.5F);

                                //TODO: ADD BLEED
                            }
                        }
                    }
            );
            this.addEventBuff(
                    caster,
                    SBSkills.NIGHTBLADE,
                    BuffCategory.BENEFICIAL,
                    SpellEventListener.Events.DEALT_DAMAGE_POST,
                    NIGHTBLADE_POST,
                    post -> {
                        LivingEntity attacker = post.getAttacker();
                        if (post.getTarget() instanceof LivingEntity target) {
                            if (context.hasSkill(SBSkills.NUMBING_POISON)) {
                                this.addSkillBuff(
                                        target,
                                        SBSkills.NUMBING_POISON,
                                        NUMBING_POISON,
                                        BuffCategory.HARMFUL,
                                        SkillBuff.ATTRIBUTE_MODIFIER,
                                        new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(NUMBING_POISON, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                        60
                                );
                            }

                            if (context.hasSkill(SBSkills.VAMPIRIC_BLADE)) {
                                this.heal(attacker, this.bonusDamage * 0.5F);
                            }

                            if (context.hasSkill(SBSkills.MUFFLE)) {
                                this.addSkillBuff(
                                        target,
                                        SBSkills.MUFFLE,
                                        MUFFLE,
                                        BuffCategory.HARMFUL,
                                        SkillBuff.MOB_EFFECT,
                                        new SBEffectInstance(caster, SBEffects.SILENCED, 60),
                                        60
                                );
                            }

                            BlockPos targetPos = target.blockPosition();
                            if (context.hasSkillReady(SBSkills.ASSASSINS_BOUNTY)
                                    && caster instanceof Player player
                                    && this.isCrit(player, target)
                                    && this.isAttackFromBehind(target, post.getSource())
                                    && this.isDark(level, player.blockPosition())
                                    && !target.isDeadOrDying()) {
                                RitualHelper.createItem(level, targetPos.above(2), new ItemStack(SBItems.FOOL_SHARD.get()));
                                this.addCooldown(SBSkills.ASSASSINS_BOUNTY, 24000);
                            }

                            if (context.hasSkill(SBSkills.SMOKE_BOMB)) {
                                ShadowVeilSpell spell = SBSpells.SHADOW_VEIL.get().createSpell();
                                spell.setMistPos(caster.position());
                                spell.softCastSpell(caster);
                            }
                        }
                    }
            );

            if (context.hasSkill(SBSkills.EVASIVE_STANCE)) {
                this.addEventBuff(
                        caster,
                        SBSkills.EVASIVE_STANCE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.INCOMING_DAMAGE,
                        EVASIVE_STANCE,
                        incomingDamage -> {
                            incomingDamage.cancelEvent();
                            // TODO: Play VFX
                            this.removeSkillBuff(caster, SBSkills.EVASIVE_STANCE);
                        }
                );
            }

            if (context.hasSkill(SBSkills.UMBRAL_SIGHT)) {
                this.addSkillBuff(
                        caster,
                        SBSkills.UMBRAL_SIGHT,
                        UMBRAL_SIGHT,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new SBEffectInstance(caster, MobEffects.NIGHT_VISION, this.getDuration()),
                        this.getDuration()
                );
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        this.removeSkillBuff(context.getCaster(), SBSkills.NIGHTBLADE);
        this.removeSkillBuff(context.getCaster(), SBSkills.EVASIVE_STANCE);
        this.removeSkillBuff(context.getCaster(), SBSkills.UMBRAL_SIGHT);
    }

    @Override
    protected void onUseImbuement(SpellContext context) {

    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected Imbuement createImbuement(SpellContext context) {
        int charges = context.hasSkill(SBSkills.LINGERING_EDGE) ? 3 : 1;
        this.bonusDamage = potency(3.0F + this.level() * 0.5F);
        DamageInstance instance = DamageInstance.of(SBDamageTypes.SB_GENERIC, this.bonusDamage);
        return Imbuement.create(this, instance, charges);
    }

    private boolean isAttackFromBehind(LivingEntity target, DamageSource source) {
        Vec3 sourceLocation = source.getSourcePosition();
        if (sourceLocation != null) {
            Vec3 viewVector = target.getViewVector(1.0F);
            viewVector = viewVector.subtract(0, viewVector.y, 0).normalize();
            Vec3 toSourceLocation = sourceLocation.subtract(target.position()).normalize();

            return toSourceLocation.dot(viewVector) < 0.0D;
        }

        return false;
    }

    private boolean isCrit(Player caster, LivingEntity target) {
        return caster.getAttackStrengthScale(0.5F) > 0.9
                && caster.fallDistance > 0.0F
                && !caster.onGround()
                && !caster.onClimbable()
                && !caster.isInWater()
                && !caster.hasEffect(MobEffects.BLINDNESS)
                && !caster.isPassenger()
                && target instanceof LivingEntity
                && !caster.isSprinting();
    }

    private boolean isDark(Level level, BlockPos blockPos) {
        int i = level.getRawBrightness(blockPos, 0) + level.getBrightness(LightLayer.BLOCK, blockPos) - level.getSkyDarken();
        return i <= 9;
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
