package com.ombremoon.spellbound.common.world.spell.summon;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.mojang.datafixers.types.templates.Sum;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.world.entity.living.SummonedWolf;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.concurrent.atomic.AtomicInteger;

public class SummonWolfPackSpell extends SummonSpell implements RadialSpell, ChargeableSpell {
    private static final ResourceLocation LONE_WOLF_ATTACK = CommonClass.customLocation("lone_wolf_attack");
    private static final ResourceLocation LONE_WOLF_SPEED = CommonClass.customLocation("lone_wolf_speed");
    private static final ResourceLocation LONE_WOLF_JUMP = CommonClass.customLocation("lone_wolf_jump");
    private static final ResourceLocation LONE_WOLF_FALL = CommonClass.customLocation("lone_wolf_fall");
    public static Builder<SummonWolfPackSpell> createSummonWolfPackSpellBuilder() {
        return createSummonBuilder(SummonWolfPackSpell.class)
                .additionalCondition((context, summonWolfPackSpell) -> {
                    var handler = context.getSpellHandler();
                    var list = handler.getActiveSpellsFromType(SBSpells.SUMMON_WOLF_PACK.get(), wolfPack -> wolfPack.isChoice(SBSkills.LONE_WOLF));
                    if (!list.isEmpty()) {
                        list.forEach(AbstractSpell::endSpell);
                        return false;
                    }

                    return (!context.isChoice(SBSkills.LONE_WOLF) && !context.isChoice(SBSkills.FENRIR)) || summonWolfPackSpell.hasFilledSoulShard(context);
                })
                .duration(2400)
                .isSummonChoice();
    }

    public SummonWolfPackSpell() {
        super(SBSpells.SUMMON_WOLF_PACK.get(), createSummonWolfPackSpellBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        int charges = Math.max(1, this.getCharges());
        if (!level.isClientSide) {
            if (context.isChoice(SBSkills.LONE_WOLF) && this.useFilledSoulShard(context)) {
                this.addSkillBuff(
                        caster,
                        SBSkills.LONE_WOLF,
                        LONE_WOLF_ATTACK,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.ATTACK_DAMAGE, new AttributeModifier(LONE_WOLF_ATTACK, 3.0, AttributeModifier.Operation.ADD_VALUE))
                );
                this.addSkillBuff(
                        caster,
                        SBSkills.LONE_WOLF,
                        LONE_WOLF_SPEED,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(LONE_WOLF_SPEED, 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                );
                this.addSkillBuff(
                        caster,
                        SBSkills.LONE_WOLF,
                        LONE_WOLF_JUMP,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.JUMP_STRENGTH, new AttributeModifier(LONE_WOLF_JUMP, 0.35, AttributeModifier.Operation.ADD_VALUE))
                );
                this.addSkillBuff(
                        caster,
                        SBSkills.LONE_WOLF,
                        LONE_WOLF_FALL,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(LONE_WOLF_FALL, 2, AttributeModifier.Operation.ADD_VALUE))
                );
            } else if (context.isChoice(SBSkills.FENRIR) && this.hasFilledSoulShard(context)) {

            } else {
                EntityType<?> wolf = SBEntities.SPECTRAL_WOLF.get();
                if (context.isChoice(SBSkills.HELLHOUND)) {
                    wolf = SBEntities.HELLHOUND.get();
                } else if (context.isChoice(SBSkills.SHADOW_WOLF)) {
                    wolf = SBEntities.SHADOW_WOLF.get();
                }

                MutableInt alphaId = new MutableInt();
                float radius = 1.5F;
                Vec3 origin = caster.position();
                float yaw = caster.getYRot();
                for (int i = 0; i < charges; i++) {
                    Vec3 spawnOffset = this.getSurroundingSpawnPosition(origin, yaw, radius, i, charges);
                    int currentIndex = i;
                    Entity spawnedEntity = this.summonEntity(context, wolf, spawnOffset, entity -> {
                        if (context.hasSkill(SBSkills.PACK_LEADER) && charges > 1) {
                            SummonedWolf summonedWolf = (SummonedWolf) entity;
                            if (currentIndex == 0) {
                                alphaId.add(summonedWolf.getId());
                                summonedWolf.setPackLeader();
                            } else {
                                summonedWolf.setPackLeaderId(alphaId.intValue());
                            }

                            entity.setYRot(caster.getYRot());
                        }
                    });

                    this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("summon_general"), spawnedEntity.getId(), EntityEffectExecutor.AutoRotate.NONE)
                            .setOffset(0, -0.5, 0));
                }
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (context.hasSkill(SBSkills.LONE_WOLF)) {
                this.removeSkillBuff(caster, SBSkills.LONE_WOLF.value());
            }
        }
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        LivingEntity caster = context.getCaster();
    }

    @Override
    public int maxCharges(SpellContext context) {
        int defaultMax = context.getSpellLevel() + 1;
        int currentSummonSize = this.getSummonSize(context);
        return Math.max(0, defaultMax - currentSummonSize);
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return context.getSpellLevel() > 0 && !this.hasSummonChoice(context) && !context.isChoice(SBSkills.LONE_WOLF) && !context.isChoice(SBSkills.FENRIR);
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
