package com.ombremoon.spellbound.common.world.spell.ruin.ice;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.spell.IceBolt;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

public class IceBoltSpell extends AnimatedSpell implements RadialSpell, ChargeableSpell {
    private static final SpellDataKey<Vector3f> HAIL_POS = SyncedSpellData.registerDataKey(IceBoltSpell.class, SBDataTypes.VECTOR3.get());
    private static final ResourceLocation CHILLING_AFTERMATH = CommonClass.customLocation("chilling_aftermath");
    public static Builder<IceBoltSpell> createIceBoltBuilder() {
        return createSimpleSpellBuilder(IceBoltSpell.class)
                .baseDamage(3.0F)
                .castCondition((context, iceBoltSpell) -> {
                    if (iceBoltSpell.isChoice(SBSkills.HAIL_STRIKE)) {
                        double range = iceBoltSpell.getCastRange();
                        Entity target = iceBoltSpell.getTargetEntity(context.getCaster(), range);
                        if (target instanceof LivingEntity livingTarget && SpellUtil.CAN_ATTACK_ENTITY.test(context.getCaster(), livingTarget)) {
                            iceBoltSpell.setHailPos(target.position());
                            return true;
                        }

                        Vec3 spawnPos = iceBoltSpell.getSpawnVec(range);
                        if (spawnPos != null) {
                            iceBoltSpell.setHailPos(spawnPos);
                            return true;
                        }

                        return false;
                    } else {
                        var handler = context.getSpellHandler();
                        List<IceBoltSpell> spells = handler.getActiveSpellsFromType(SBSpells.ICE_BOLT.get());
                        for (IceBoltSpell spell : spells) {
                            if (spell.isChoice(SBSkills.ICY_JAVELIN)) {
                                if (context.isChoice(SBSkills.ICY_JAVELIN)) {
                                    spell.endSpell();
                                }

                                return false;
                            }
                        }

                        return true;
                    }
                });
    }

    public IceBoltSpell() {
        super(SBSpells.ICE_BOLT.get(), createIceBoltBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(HAIL_POS, new Vector3f());
    }

    public void setHailPos(Vec3 pos) {
        this.spellData.set(HAIL_POS, new Vector3f((float)pos.x(), (float)pos.y(), (float)pos.z()));
    }

    public Vec3 getHailPos() {
        Vector3f pos = this.spellData.get(HAIL_POS);
        return new Vec3(pos.x(), pos.y(), pos.z());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        if (!level.isClientSide) {
            if (this.isChoice(SBSkills.ICY_JAVELIN)) {

            } else {
                if (context.isChoice(SBSkills.GLACIAL_VOLLEY)) {
                    var handler = context.getSpellHandler();
                    handler.setChargingOrChannelling(true);
                }

                int count = this.isChoice(SBSkills.ICE_RING) ? 8 : 1;
                for (int i = 0; i < count; i++) {
                    float yRot = this.getBoltAngle(caster.getYRot(), i, count);
                    float xRot = context.isChoice(SBSkills.ICE_RING) ? 0 : caster.getXRot();
                    this.shootProjectile(context, SBEntities.ICE_BOLT.get(), xRot, yRot, 1.25F, 1.0F, iceBolt -> {
                        iceBolt.setSize(Mth.clamp(this.getCharges(), 1, 3));
                        if (context.hasSkill(SBSkills.FROST_PIERCER))
                            iceBolt.setPierceLevel((byte) 2);
                    });
                }
            }
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (this.isChoice(SBSkills.GLACIAL_VOLLEY) && this.tickCount % 5 == 1) {
                this.shootProjectile(context, SBEntities.ICE_BOLT.get(), 1.5F, 1.0F, iceBolt -> {
                    if (context.hasSkill(SBSkills.FROST_PIERCER))
                        iceBolt.setPierceLevel((byte) 2);
                });
            } else if (this.isChoice(SBSkills.HAIL_STRIKE) && this.tickCount % 5 == 0) {
                Vec3 pos = this.getHailPos().add(RandomUtil.randomValueBetween(0, 2), 5, RandomUtil.randomValueBetween(0, 2));
                this.shootProjectile(context, SBEntities.ICE_BOLT.get(), pos, 90, 0, 1.25F, 1.0F, iceBolt -> {
                    if (context.hasSkill(SBSkills.FROST_PIERCER))
                        iceBolt.setPierceLevel((byte) 2);
                });

            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    public void onProjectileHitEntity(ISpellEntity<?> spellEntity, SpellContext context, EntityHitResult result) {
        if (spellEntity instanceof IceBolt bolt) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                Entity entity = result.getEntity();
                if (entity instanceof LivingEntity livingEntity) {
                    float damage = this.getBaseDamage() * (1.0F + (bolt.getSize() - 1) * 0.75F);
                    if (context.hasSkill(SBSkills.FROSTBITE_SYNERGY) && (livingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED) < livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) || this.hasFrostEffect(livingEntity))) {
                        damage *= 1.25F;
                    }

                    if (this.hurt(bolt, livingEntity, damage)) {
                        var handler = SpellUtil.getSpellHandler(livingEntity);
                        double slowDebuff = -0.05;
                        var optional = handler.getSkillBuff(SBSkills.ICE_BOLT.value());
                        if (context.hasSkill(SBSkills.WINTER_IS_COMING) && optional.isPresent() && optional.get().object() instanceof ModifierData modifierData) {
                            slowDebuff += modifierData.attributeModifier().amount();
                        }

                        this.addSkillBuff(
                                livingEntity,
                                SBSkills.ICE_BOLT,
                                this.location(),
                                BuffCategory.HARMFUL,
                                SkillBuff.ATTRIBUTE_MODIFIER,
                                new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(this.location(), Math.max(slowDebuff, -0.25), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                100
                        );

                        if (context.hasSkill(SBSkills.CHILLING_AFTERMATH) && livingEntity instanceof Player) {
                            this.addSkillBuff(
                                    livingEntity,
                                    SBSkills.CHILLING_AFTERMATH,
                                    CHILLING_AFTERMATH,
                                    BuffCategory.HARMFUL,
                                    SkillBuff.ATTRIBUTE_MODIFIER,
                                    new ModifierData(SBAttributes.CAST_SPEED, new AttributeModifier(CHILLING_AFTERMATH, -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                    100
                            );
                        }

                        if (context.hasSkill(SBSkills.SHATTERING_IMPACT)) {
                            var effects = SpellUtil.getSpellEffects(livingEntity);
                            effects.incrementBuildEffects(EffectManager.Effect.FROST, 10);
                        }

                        if (bolt.getPierceLevel() <= 0) {
                            bolt.discard();
                        }
                    }
                }
            }
        }
    }

    private boolean hasFrostEffect(LivingEntity livingEntity) {
        Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().is(SBTags.MobEffects.FROST))
                return true;
        }

        return false;
    }

    private float getBoltAngle(float startAngle, int i, int count) {
        float spread = 45.0F;
        float totalSpread = spread * (count - 1);
        float startOffset = -totalSpread / 2.0F;
        return startAngle + startOffset + (i * spread);
    }

    @Override
    public int maxCharges(SpellContext context) {
        return 3;
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return context.isChoice(SBSkills.PERMAFROST_LANCE);
    }

    @Override
    protected int getDuration(SpellContext context) {
        if (this.isChoice(SBSkills.GLACIAL_VOLLEY)) {
            return -1;
        } else if (this.isChoice(SBSkills.HAIL_STRIKE)) {
            return 100;
        }

        return this.isChoice(SBSkills.ICY_JAVELIN) ? 1200 : super.getDuration(context);
    }

    @Override
    public int getCastTime(SpellContext context) {
        if (this.canCharge(context)) {
            return 60;
        }

        return super.getCastTime(context);
    }

    @Override
    public CastType getCastType(SpellContext context) {
        return this.isChoice(SBSkills.GLACIAL_VOLLEY) ? CastType.CHANNEL : super.getCastType(context);
    }

    @Override
    protected boolean shouldRender(SpellContext context) {
        return this.isChoice(SBSkills.GLACIAL_VOLLEY) || this.isChoice(SBSkills.ICY_JAVELIN) || this.isChoice(SBSkills.HAIL_STRIKE);
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
