package com.ombremoon.spellbound.common.world.spell.ruin.shock;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Set;

public class ElectricChargeSpell extends AnimatedSpell {
    private static final SpellDataKey<Integer> DISCHARGE_TICK = SyncedSpellData.registerDataKey(ElectricChargeSpell.class, SBDataTypes.INT.get());
    public static Builder<ElectricChargeSpell> createElectricChargeBuilder() {
        return createSimpleSpellBuilder(ElectricChargeSpell.class)
                .duration(200)
                .manaCost(5)
                .baseDamage(6)
                .castType(CastType.INSTANT)
                .castCondition((context, spell) -> {
                    Entity entity = context.getTarget();
                    if (spell.discharging)
                        return false;

                    if (spell.entityIds.size() >= context.getSpellLevel() + 1) {
                        spell.discharged = true;
                        return context.isRecast();
                    } else if (entity != null) {
                        if (spell.entityIds.contains(entity.getId()))
                            spell.discharged = true;

                        return true;
                    } else {
                        return context.isRecast();
                    }
                })
                .instantCast()
                .fullRecast()
                .updateInterval(1);
    }

    private Set<Integer> entityIds = new IntOpenHashSet();
    private boolean discharged;
    private boolean discharging;

    public ElectricChargeSpell() {
        super(SBSpells.ELECTRIC_CHARGE.get(), createElectricChargeBuilder());
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(DISCHARGE_TICK, 0);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Entity target = context.getTarget();
        if (target != null && this.entityIds.size() < context.getSpellLevel() + 1)
            this.entityIds.add(target.getId());
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        super.onSpellRecast(context);
        Level level = context.getLevel();
        boolean hasShard = context.hasCatalyst(SBItems.STORM_SHARD.get());
        if (context.hasSkill(SBSkills.AMPLIFY)) {
            return;
        }

        if (context.getTarget() == null || this.discharged) {
            for (Integer entityId : this.entityIds) {
                Entity entity = level.getEntity(entityId);
                if (entity instanceof LivingEntity livingEntity)
                    discharge(context, livingEntity, hasShard);
            }
            endSpell();
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        var handler = context.getSpellHandler();
        if (context.hasSkill(SBSkills.AMPLIFY)) {
            if ((context.isRecast() && context.getTarget() == null) || this.discharged) {
                this.discharging = true;
                if (handler.isChargingOrChannelling()) {
                    incrementTick();
                    if (!level.isClientSide && this.tickCount % 20 == 0)
                        consumeMana(context.getCaster(), 3);
                }
            }
        }

        for (Integer entityId : this.entityIds) {
            Entity entity = level.getEntity(entityId);
            if (entity instanceof LivingEntity && entity.tickCount % 3 == 0) {
                this.createSurroundingServerParticles(entity, SBParticles.SPARK.get(), 1);
            }
        }

        if (this.entityIds.isEmpty())
            this.endSpell();
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        log(this.entityIds);
        if (context.hasSkill(SBSkills.AMPLIFY) && (context.isRecast() && context.getTarget() == null) || this.discharged) {
            boolean hasShard = context.hasCatalyst(SBItems.STORM_SHARD.get());
            for (Integer entityId : this.entityIds) {
                Entity entity = context.getLevel().getEntity(entityId);
                if (entity instanceof LivingEntity livingEntity)
                    discharge(context, livingEntity, hasShard);
            }
        }
    }

    private void discharge(SpellContext context, LivingEntity target, boolean hasShard) {
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        var handler = context.getSpellHandler();
        float damage = this.getBaseDamage();
        if (context.hasSkill(SBSkills.ELECTRIFICATION))
            handler.applyStormStrike(target, 60);

        var entities = level.getEntities(target, target.getBoundingBox().inflate(4), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        if (!level.isClientSide) {
            if (context.hasSkillReady(SBSkills.OSCILLATION)) {
                if (caster instanceof Player player) {
                    for (ItemStack itemStack : player.getInventory().items) {
                        if (itemStack.is(SBItems.STORM_SHARD.get())) {
                            damage *= 1.05F * itemStack.getCount();
                            player.getInventory().removeItem(itemStack);
                        }
                    }
                }
                addCooldown(SBSkills.OSCILLATION, 600);
            }
            float amplify = 1.0F + getPowerForTime(getDischargeTick(), 60);
            damage *= amplify;

            if (hurt(target, damage)) {
                if (target.isDeadOrDying()) {
                    if (context.hasSkill(SBSkills.STORM_SURGE))
                        handler.awardMana(10 + (context.getSpellLevel() * 2));

                    if (context.hasSkill(SBSkills.UNLEASHED_STORM)) {
                        this.spawnDischargeParticles(target);
                        for (Entity entity : entities) {
                            if (entity instanceof LivingEntity targetEntity) {
                                if (!isCaster(targetEntity)
                                        && hurt(targetEntity, damage / 2)
                                        && targetEntity.isDeadOrDying()
                                        && context.hasSkillReady(SBSkills.PIEZOELECTRIC)
                                        && caster instanceof Player) {
                                    RitualHelper.createItem(level, targetEntity.position(), new ItemStack(SBItems.STORM_SHARD.get()));
                                    this.addCooldown(SBSkills.PIEZOELECTRIC, 600);
                                }
                            }
                        }
                    }

                    if (context.hasSkill(SBSkills.PIEZOELECTRIC) && caster instanceof Player) {
                        RitualHelper.createItem(level, target.position(), new ItemStack(SBItems.STORM_SHARD.get()));
                        this.addCooldown(SBSkills.PIEZOELECTRIC, 600);
                    }
                }
            }

            if (!checkForCounterMagic(target)) {
                if (context.hasSkill(SBSkills.ELECTRIFICATION))
                    handler.applyStormStrike(target, 60);

                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity livingEntity) {
                        if (context.hasSkill(SBSkills.CHAIN_REACTION)) {
                            if (!this.entityIds.contains(livingEntity.getId())) {
                                this.entityIds.add(livingEntity.getId());
                                discharge(context, livingEntity, hasShard);
                            }
                        }

                        if (context.hasSkill(SBSkills.HIGH_VOLTAGE) && hasShard) {
                            if (!isCaster(livingEntity))
                                addSkillBuff(
                                        livingEntity,
                                        SBSkills.HIGH_VOLTAGE,
                                        BuffCategory.HARMFUL,
                                        SkillBuff.MOB_EFFECT,
                                        new MobEffectInstance(SBEffects.STUNNED, 60, 0, false, false),
                                        60
                                );
                        }
                    }
                }

                if (context.hasSkill(SBSkills.HIGH_VOLTAGE) && hasShard) {
                    addSkillBuff(
                            target,
                            SBSkills.HIGH_VOLTAGE,
                            BuffCategory.HARMFUL,
                            SkillBuff.MOB_EFFECT,
                            new MobEffectInstance(SBEffects.STUNNED, 60, 0, false, false),
                            60
                    );

                    context.useCatalyst(SBItems.STORM_SHARD.get());
                    addCooldown(SBSkills.HIGH_VOLTAGE, 600);
                }

                if (context.hasSkill(SBSkills.ALTERNATING_CURRENT)) {
                    if (RandomUtil.percentChance(potency(0.03F)) && target.getHealth() < caster.getHealth() * 2) {
                        target.kill();
                        if (context.hasSkill(SBSkills.PIEZOELECTRIC) && caster instanceof Player) {
                            RitualHelper.createItem(level, target.position(), new ItemStack(SBItems.STORM_SHARD.get()));
                            this.addCooldown(SBSkills.PIEZOELECTRIC, 600);
                        }
                    } else {
                        hurt(caster, caster.getMaxHealth() * 0.05F);
                    }
                }
            }
        }
    }

    private void spawnDischargeParticles(Entity entity) {
        for (int i = 0; i < 25; i++) {
            double d0 = entity.getRandom().nextFloat() * 0.4;
            double d1 = entity.getRandom().nextFloat() * 0.4;
            double d2 = entity.getRandom().nextFloat() * 0.4;
            this.createServerParticles(SBParticles.SPARK.get(), entity.xo, entity.getY(), entity.zo, d0, d1, d2);
        }
    }

    private int getDischargeTick() {
        return this.spellData.get(DISCHARGE_TICK);
    }

    private void incrementTick() {
        this.spellData.set(DISCHARGE_TICK, getDischargeTick() + 1);
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        compoundTag.putIntArray("ChargedTargets", this.entityIds.stream().toList());
        compoundTag.putBoolean("Discharged", this.discharged);
        compoundTag.putBoolean("Discharging", this.discharging);
        return compoundTag;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        this.entityIds = new IntOpenHashSet(nbt.getIntArray("ChargedTargets"));
        this.discharged = nbt.getBoolean("Discharged");
        this.discharging = nbt.getBoolean("Discharging");
    }
}
