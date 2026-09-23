package com.ombremoon.spellbound.common.world.spell.divine;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.ImbuementSpell;
import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SmiteSpell extends ImbuementSpell {
    private static final ResourceLocation GOLDEN_PARRY = CommonClass.customLocation("golden_parry");
    private static final ResourceLocation REFLECTIVE_JUDGEMENT = CommonClass.customLocation("reflective_judgement");
    private static final ResourceLocation BLACK_BLADE = CommonClass.customLocation("black_blade");
    private static final ResourceLocation BLACK_BLADE_DEBUFF = CommonClass.customLocation("black_blade_debuff");
    private long parryTick;

    public static Builder<SmiteSpell> createSmiteBuilder() {
        return createImbuementSpellBuilder(SmiteSpell.class)
                .duration(1200)
                .negativeScaling((context, smiteSpell) -> smiteSpell.isChoice(SBSkills.BLACK_BLADE))
                .imbuementEffect((context, smiteSpell) -> {
                    LivingEntity caster = context.getCaster();
                    ResourceLocation effect = CommonClass.customLocation("smite_cast");
                    if (smiteSpell.isChoice(SBSkills.BLACK_BLADE)) {
                        effect = CommonClass.customLocation("smite_dark_blade_cast");
                    }

                    return EffectData.Entity.of(effect, caster.getId(), EntityEffectExecutor.AutoRotate.NONE)
                            .setOffset(0, -caster.getEyeHeight(), 0);
                    }
                );
    }

    public SmiteSpell() {
        super(SBSpells.SMITE.get(), createSmiteBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        SoundEvent sound = SpellboundSounds.SMITE.get();
        float volume = 0.5F + level.random.nextFloat() * 0.3F;
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;

        if (!level.isClientSide) {
            if (this.isChoice(SBSkills.BLACK_BLADE)) {
                this.addEventBuff(
                        caster,
                        SBSkills.BLACK_BLADE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.ATTACK_POST,
                        BLACK_BLADE,
                        attackEvent -> {
                            if (this.isChoice(SBSkills.BLACK_BLADE) && this.isHoldingImbuement(caster) && attackEvent.getTarget() instanceof LivingEntity living) {
                                this.addBlackBladeDebuff(living);
                            }
                        }
                );
            }
            if (context.hasSkill(SBSkills.GOLDEN_PARRY)) {
                this.addEventBuff(
                        caster,
                        SBSkills.GOLDEN_PARRY,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.INCOMING_DAMAGE,
                        GOLDEN_PARRY,
                        incomingDamage -> {
                            Entity source = incomingDamage.getSource().getDirectEntity();
                            boolean parrySuccess = false;
                            if (source instanceof LivingEntity living && Math.abs(this.parryTick - living.getData(SBData.ATTACK_START)) < 8) {
                                living.knockback(0.4, caster.getX() - living.getX(), caster.getZ() - living.getZ());
                                this.parryTick = 0;

                                if (context.hasSkill(SBSkills.SACRED_BLADE)) {
                                    boolean blessedArc = context.hasSkill(SBSkills.BLESSED_ARC);
                                    this.shootProjectile(context, SBEntities.SACRED_BLADE.get(), blessedArc ? 3F : 1.5F, 1.0F, sacredBlade -> {
                                        if (blessedArc) {
                                            sacredBlade.setSize(3);
                                        }
                                    });
                                }

                                if (context.hasSkill(SBSkills.REFLECTIVE_JUDGEMENT)) {
                                    this.addEventBuff(
                                            living,
                                            SBSkills.REFLECTIVE_JUDGEMENT,
                                            BuffCategory.HARMFUL,
                                            SpellEventListener.Events.PRE_DAMAGE,
                                            REFLECTIVE_JUDGEMENT,
                                            pre -> {
                                                Entity sourceEntity = pre.getSource().getEntity();
                                                if (sourceEntity != null && sourceEntity.is(caster)) {
                                                    pre.setNewDamage(pre.getOriginalDamage() * 1.2F);
                                                    this.removeSkillBuff(living, SBSkills.REFLECTIVE_JUDGEMENT);
                                                }
                                            },
                                            100
                                    );
                                }

                                parrySuccess = true;
                            } else if (source instanceof Projectile projectile && Math.abs(this.parryTick - caster.level().getGameTime()) < 8) {
                                Vec3 vec = projectile.getDeltaMovement();
                                projectile.deflect(ProjectileDeflection.NONE, caster, caster, caster instanceof Player);
                                this.parryTick = 0;
                                parrySuccess = true;
                                projectile.discard();

                                if (context.hasSkill(SBSkills.REFLECTIVE_JUDGEMENT)) {
                                    Projectile projectile1 = (Projectile) projectile.getType().create(level);
                                    projectile1.setPos(caster.getEyePosition());
                                    projectile1.setOwner(caster);
                                    projectile1.setDeltaMovement(vec.scale(-1));
                                    level.addFreshEntity(projectile1);
                                }
                            }

                            if (parrySuccess) {
                                incomingDamage.cancelEvent();
                                this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("smite_parry"), caster.getId(), EntityEffectExecutor.AutoRotate.NONE)
                                        .setOffset(0, -0.5, 0)
                                        .setAllowMulti(true));
                                level.playSound(null, context.getCaster().blockPosition(), SpellboundSounds.SMITE_PARRY.get(),
                                        SoundSource.PLAYERS, volume, pitch);
                            }
                        }
                );
            }

            if(this.isChoice(SBSkills.BLACK_BLADE)) {
                sound = SpellboundSounds.SMITE_DARK_BLADE.get();
            }

            level.playSound(null, context.getCaster().blockPosition(), sound, SoundSource.PLAYERS, volume, pitch);
        }
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        super.onSpellRecast(context);
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.swapImbuementEffect(context);
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.removeSkillBuff(caster, SBSkills.GOLDEN_PARRY);
            this.removeSkillBuff(caster, SBSkills.BLACK_BLADE);
        }
    }

    @Override
    protected void onUseImbuement(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        var handler = context.getSpellHandler();
        if (!level.isClientSide) {
            if (context.isChoice(SBSkills.GOLDEN_PARRY) || !handler.inCastMode() && context.hasSkill(SBSkills.GOLDEN_PARRY)) {
                this.parryTick = level.getGameTime();
                if (!handler.inCastMode() && caster instanceof Player player) {
                    SpellAnimation anim = new SpellAnimation("simple_cast", SpellAnimation.Type.CAST, true, false);
                    this.playAnimation(player, anim);
                }
            } else if (context.isChoice(SBSkills.SACRED_BLADE) && context.hasSkillReady(SBSkills.SACRED_BLADE)) {
                boolean blessedArc = context.hasSkill(SBSkills.BLESSED_ARC);
                this.shootProjectile(context, SBEntities.SACRED_BLADE.get(), blessedArc ? 3F : 1.5F, 1.0F, sacredBlade -> {
                    if (blessedArc) {
                        sacredBlade.setSize(3);
                    }
                });

                this.consumeMana(caster, 10);
                this.addCooldown(SBSkills.SACRED_BLADE, 20);
            } else if (context.isChoice(SBSkills.PRAYERFUL_STRIKE) && context.hasSkillReady(SBSkills.PRAYERFUL_STRIKE)) {
                var entities = this.getNearbyEntities(2);
                for (LivingEntity entity : entities) {
                    if (!this.isChoice(SBSkills.BLACK_BLADE)) {
                        if (this.getAttackPredicate().test(entity) && entity.getType().is(EntityTypeTags.UNDEAD)) {
                            this.hurt(entity, SBDamageTypes.SB_GENERIC, 3F);
                        } else if (this.getAllyPredicate().test(entity) && !entity.is(caster)) {
                            this.heal(entity, 3F);
                        }
                    } else {
                        if (this.hurt(entity, SBDamageTypes.SB_GENERIC, 3F)) {
                            this.addBlackBladeDebuff(entity);
                        }
                    }
                }

                if (context.hasSkill(SBSkills.GOLDEN_LAND)) {
                    float radius = 1.5F;
                    Vec3 origin = caster.position();
                    float yaw = caster.getYRot();
                    for (int i = 0; i < 5; i++) {
                        Vec3 spawnOffset = this.getSurroundingSpawnPosition(origin, yaw, radius, i, 5);
                        this.summonEntity(context, SBEntities.GOLDEN_DART.get(), spawnOffset);
                    }
                }

                this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("smite_prayerful_strike"), context.getCaster().getId(), EntityEffectExecutor.AutoRotate.NONE)
                        .setOffset(0, 0.1, 0)
                        .setAllowMulti(true)
                        .setScale(2, 1, 2));

                this.consumeMana(caster, 15);
                this.addCooldown(SBSkills.PRAYERFUL_STRIKE, 60);
            }
        }
    }

    @Override
    protected Imbuement createImbuement(SpellContext context) {
        return context.isChoice(SBSkills.BLACK_BLADE)
                ? Imbuement.create(this, DamageInstance.of(SBDamageTypes.SB_GENERIC, 3F), -1, CommonClass.customLocation("smite_black_blade"))
                : Imbuement.create(this, DamageInstance.of(SBDamageTypes.SB_GENERIC, 3F, EntityTypeTags.UNDEAD), -1, this.location());
    }

    @Override
    public boolean isMainChoice(SpellContext context) {
        return this.isChoice(SBSkills.BLACK_BLADE) || super.isMainChoice(context);
    }

    @Override
    protected int getDuration(SpellContext context) {
        int duration = super.getDuration(context);
        return context.hasSkill(SBSkills.OATHSWORN) ? duration * 2 : duration;
    }

    private void swapImbuementEffect(SpellContext context) {
        LivingEntity caster = context.getCaster();
        if (this.isChoice(SBSkills.BLACK_BLADE)) {
            this.removeImbuementEffect(caster, CommonClass.customLocation("smite_cast"));
        } else {
            this.removeImbuementEffect(caster, CommonClass.customLocation("smite_dark_blade_cast"));
        }

        this.triggerImbuementEffect(caster, this.getImbuementEffect(context));
    }

    public void addBlackBladeDebuff(LivingEntity target) {
        var handler = SpellUtil.getSpellHandler(target);
        var optional = handler.getSkillBuff(SBSkills.BLACK_BLADE.value());
        double healthDebuff = -0.05;
        if (optional.isPresent() && optional.get().object() instanceof ModifierData modifierData) {
            healthDebuff = Math.max(healthDebuff + modifierData.attributeModifier().amount(), -0.5);
        }

        this.addSkillBuff(
                target,
                SBSkills.BLACK_BLADE,
                BLACK_BLADE_DEBUFF,
                BuffCategory.HARMFUL,
                SkillBuff.ATTRIBUTE_MODIFIER,
                new ModifierData(Attributes.MAX_HEALTH, new AttributeModifier(BLACK_BLADE_DEBUFF, healthDebuff, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                200
        );
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}

