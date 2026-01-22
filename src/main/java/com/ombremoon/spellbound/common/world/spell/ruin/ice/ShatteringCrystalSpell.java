package com.ombremoon.spellbound.common.world.spell.ruin.ice;

import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.spell.IceShrapnel;
import com.ombremoon.spellbound.common.world.entity.spell.ShatteringCrystal;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.List;
import java.util.function.Predicate;

public class ShatteringCrystalSpell extends AnimatedSpell {
    protected static final SpellDataKey<Integer> CRYSTAL = SyncedSpellData.registerDataKey(ShatteringCrystalSpell.class, SBDataTypes.INT.get());
    private static final ResourceLocation FRIGID_BLAST = CommonClass.customLocation("frigid_blast");
    private static final ResourceLocation HYPOTHERMIA = CommonClass.customLocation("hypothermia");
    private static final Predicate<SpellContext> CRYSTAL_PREDICATE = context -> context.getTarget() instanceof ShatteringCrystal crystal && context.getCaster() == crystal.getOwner();

    public static Builder<ShatteringCrystalSpell> createShatteringCrystalBuild() {
        return createSimpleSpellBuilder(ShatteringCrystalSpell.class)
                .duration(400)
                .manaCost(20)
                .baseDamage(5)
                .castAnimation(context -> context.quickOrSimpleCast(CRYSTAL_PREDICATE.test(context)))
                .castCondition((context, shatteringCrystalSpell) -> {
                    var skills = context.getSkills();
                    if (context.getTarget() instanceof ShatteringCrystal crystal && context.getCaster() == crystal.getOwner()) {
                        if (skills.hasSkill(SBSkills.GLACIAL_IMPACT) && context.hasCatalyst(SBItems.FROZEN_SHARD.get()) && !crystal.marked) {
                            crystal.marked = true;
                            context.useCatalyst(SBItems.FROZEN_SHARD.get());
                        } else {
                            primeCrystal(context, crystal);
                        }

                        return false;
                    }

                    return shatteringCrystalSpell.hasValidSpawnPos();
                });
    }

    private int spawnTick = 100;
    private boolean primed;
    private int primeTick = 50;
    private int primeCount = 0;

    public ShatteringCrystalSpell() {
        super(SBSpells.SHATTERING_CRYSTAL.get(), createShatteringCrystalBuild());
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(CRYSTAL, 0);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            ShatteringCrystal crystal = this.summonEntity(context, SBEntities.SHATTERING_CRYSTAL.get(), shatteringCrystal -> shatteringCrystal.setStartTick(100));
            this.setCrystal(crystal.getId());
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        var skills = context.getSkills();
        if (!level.isClientSide) {
            ShatteringCrystal crystal = this.getCrystal(context);
            if (!this.isSpawning()) {
                if (crystal != null && (skills.hasSkill(SBSkills.THIN_ICE) || skills.hasSkill(SBSkills.CHILL))) {
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, crystal.getBoundingBox().inflate(4))
                            .stream()
                            .filter(livingEntity -> !this.isCaster(livingEntity) || SpellUtil.IS_ALLIED.test(caster, livingEntity))
                            .toList();

                    if (skills.hasSkill(SBSkills.CHILL) && this.tickCount % 20 == 0) {
                        for (LivingEntity entity : entities) {
                            this.hurt(crystal, entity, this.getBaseDamage() / 2);
                        }
                    }

                    if (skills.hasSkill(SBSkills.THIN_ICE) && !entities.isEmpty())
                        primeCrystal(context, crystal);
                }

                if (this.primed) {
                    if (this.primeTick > 0) {
                        this.primeTick--;
                    } else {
                        this.explodeCrystal(context);
                    }
                }
            } else {
                this.spawnTick--;
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            ShatteringCrystal crystal = this.getCrystal(context);
            if (crystal != null)
                crystal.discard();
        }
    }

    @Override
    public void onProjectileHitEntity(ISpellEntity<?> spellEntity, SpellContext context, EntityHitResult result) {
        if (spellEntity instanceof IceShrapnel shrapnel) {
            Level level = context.getLevel();
            LivingEntity caster = context.getCaster();
            if (!level.isClientSide) {
                Entity entity = result.getEntity();

                if (entity.is(caster)) return;

                if (entity instanceof LivingEntity livingEntity) {
                    this.hurt(shrapnel, livingEntity, 3.0F + shrapnel.getSize());
                    shrapnel.discard();
                }
            }
        }
    }

    @Override
    public int getCastTime(SpellContext context) {
        return context != null && CRYSTAL_PREDICATE.test(context) ? 5 : super.getCastTime(context);
    }

    @Override
    protected void registerSkillTooltips() {

    }

    private static void primeCrystal(SpellContext context, ShatteringCrystal crystal) {
        var skills = context.getSkills();
        ShatteringCrystalSpell spell = crystal.getSpell();
        int count = skills.hasSkill(SBSkills.CRYSTAL_ECHO) ? 2 : 1;
        if (spell != null && !spell.isSpawning() && spell.primeCount < count && !spell.primed) {
            spell.primed = true;
            spell.primeTick = 50;
            spell.primeCount++;
            crystal.setEndTick(52);
        }
    }

    private boolean isSpawning() {
        return this.spawnTick > 0;
    }

    private void explodeCrystal(SpellContext context) {
        Level level = context.getLevel();
        var skills = context.getSkills();
        ShatteringCrystal crystal = this.getCrystal(context);
        if (crystal != null) {
            List<Entity> entities = level.getEntities(crystal, crystal.getBoundingBox().inflate(4));
            boolean flag = skills.hasSkill(SBSkills.CRYSTAL_ECHO) && !crystal.marked;
            int count = flag ? 2 : 1;
            for (Entity entity : entities) {
                if (skills.hasSkill(SBSkills.CHAOTIC_SHATTER) && entity instanceof ShatteringCrystal crystal1 && context.getCaster() == crystal1.getOwner()) {
                    ShatteringCrystalSpell spell = crystal1.getSpell();
                    if (spell != null && spell.primeCount < count) {
                        primeCrystal(context, crystal1);
                    }
                } else if (entity instanceof LivingEntity livingEntity && !this.isCaster(livingEntity) && this.hurt(crystal, livingEntity)) {
                    if (skills.hasSkill(SBSkills.FRIGID_BLAST))
                        this.addSkillBuff(
                                livingEntity,
                                SBSkills.FRIGID_BLAST,
                                BuffCategory.HARMFUL,
                                SkillBuff.ATTRIBUTE_MODIFIER,
                                new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(FRIGID_BLAST, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                100
                        );

                    if (skills.hasSkill(SBSkills.HYPOTHERMIA))
                        this.addSkillBuff(
                                livingEntity,
                                SBSkills.HYPOTHERMIA,
                                BuffCategory.HARMFUL,
                                SkillBuff.ATTRIBUTE_MODIFIER,
                                new ModifierData(SBAttributes.FROST_SPELL_RESIST, new AttributeModifier(HYPOTHERMIA, -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                200
                        );

                    if (crystal.marked) {
                        this.addSkillBuff(
                                livingEntity,
                                SBSkills.GLACIAL_IMPACT,
                                BuffCategory.HARMFUL,
                                SkillBuff.MOB_EFFECT,
                                new MobEffectInstance(SBEffects.FROZEN, 60, 0, false, true)
                        );
                        this.addSkillBuff(
                                livingEntity,
                                SBSkills.GLACIAL_IMPACT,
                                BuffCategory.HARMFUL,
                                SkillBuff.MOB_EFFECT,
                                new MobEffectInstance(SBEffects.PERMAFROST, 200, 0, false, true)
                        );
                    }
                }
            }

            if (skills.hasSkill(SBSkills.FROZEN_SHRAPNEL)) {
                int shards = RandomUtil.randomNumberBetween(6, 12);
                for (int i = 0; i < shards; i++) {
                    this.shootProjectile(
                            context,
                            SBEntities.ICE_SHRAPNEL.get(),
                            crystal.position().add(0, 1.5F, 0),
                            (float) Math.toDegrees(RandomUtil.randomValueBetween(-Mth.PI / 4, Mth.PI / 4)),
                            (float) Math.toDegrees(RandomUtil.randomValueUpTo(Mth.TWO_PI)),
                            1.25F,
                            1.0F,
                            iceShrapnel -> iceShrapnel.setSize(RandomUtil.randomNumberBetween(0, 2))
                    );
                    this.shootProjectile(
                            context,
                            SBEntities.ICE_SHRAPNEL.get(),
                            crystal.position().add(0, 1.5F, 0),
                            0.0F,
                            (float) Math.toDegrees(i * Mth.TWO_PI / shards),
                            1.25F,
                            1.0F,
                            iceShrapnel -> iceShrapnel.setSize(RandomUtil.randomNumberBetween(0, 2))
                    );
                }
            }

            if (skills.hasSkill(SBSkills.LINGERING_FROST) && this.primeCount == count) {
                this.summonEntity(context, SBEntities.ICE_MIST.get(), crystal.position());
            }

            if (skills.hasSkillReady(SBSkills.ICE_SHARD)) {
                RitualHelper.createItem(level, crystal.position(), new ItemStack(SBItems.FROZEN_SHARD.get()));
                this.addCooldown(SBSkills.ICE_SHARD, 24000);
            }

            if (flag && this.primeCount < count) {
                this.setRemainingTicks(300);
                this.spawnTick = 100;
                ShatteringCrystal newCrystal = this.summonEntity(context, SBEntities.SHATTERING_CRYSTAL.get(), crystal.position(), shatteringCrystal -> shatteringCrystal.setStartTick(100));
                this.setCrystal(newCrystal.getId());
            } else {
                endSpell();
            }
        }

        this.primed = false;
        this.primeTick = 50;
    }

    private void setCrystal(int crystal) {
        this.spellData.set(CRYSTAL, crystal);
    }

    private ShatteringCrystal getCrystal(SpellContext context) {
        Entity entity = context.getLevel().getEntity(this.spellData.get(CRYSTAL));
        return entity instanceof ShatteringCrystal crystal ? crystal : null;
    }
}
