package com.ombremoon.spellbound.common.world.spell.divine;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.magic.api.events.DeathEvent;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.entity.spell.HealingBlossom;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.DamageEvent;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class HealingBlossomSpell extends AnimatedSpell {
    private static final SpellDataKey<Integer> BLOSSOM_ID = SyncedSpellData.registerDataKey(HealingBlossomSpell.class, SBDataTypes.INT.get());
    private static final ResourceLocation PETAL_SHIELD = CommonClass.customLocation("petal_shield");
    private static final ResourceLocation VERDANT_RENEWAL = CommonClass.customLocation("verdant_renewal");
    private static final ResourceLocation REBIRTH = CommonClass.customLocation("rebirth");
    private boolean fastBloomed = false;
    private boolean hasBursted = false;

    private static Builder<HealingBlossomSpell> createHealingBlossomSpell() {
        return createSimpleSpellBuilder(HealingBlossomSpell.class)
                .manaCost(45)
                .duration(500)
                .castTime(20)
                .castCondition((context, spell) -> spell.hasValidSpawnPos())
                .fullRecast(true);
    }

    public HealingBlossomSpell() {
        super(SBSpells.HEALING_BLOSSOM.get(), createHealingBlossomSpell());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.HEALING_BLOSSOM,
                SkillTooltip.HEAL.tooltip(this.getModifiedHeal(4)),
                SkillTooltip.RADIUS.tooltip(potency(2.5F)),
                SkillTooltip.DURATION.tooltip(500),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.CHARGE_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.BLOOM,
                SkillTooltip.CHARGE_DURATION.tooltip(-180)
        );
        this.addSkillDetails(SBSkills.ETERNAL_SPRING,
                SkillTooltip.MODIFY_DURATION.tooltip(33F)
        );
        this.addSkillDetails(SBSkills.THORNY_VINES,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, this.getModifiedDamage(4)))
        );
        this.addSkillDetails(SBSkills.FLOWER_FIELD,
                SkillTooltip.HEAL.tooltip(this.getModifiedHeal(2))
        );
        this.addSkillDetails(SBSkills.BURST_OF_LIFE,
                SkillTooltip.HEAL.tooltip(this.getModifiedHeal(4))
        );
        this.addSkillDetails(SBSkills.FLOURISHING_GROWTH,
                SkillTooltip.HEALTH_TO_MANA.tooltip(potency(150F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.PETAL_SHIELD,
                SkillTooltip.ATTRIBUTE.tooltip(
                        new ModifierData(Attributes.ARMOR, new AttributeModifier(PETAL_SHIELD, potency(0.2F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.REBIRTH,
                SkillTooltip.MAX_HEALTH_HEAL.tooltip(50F),
                SkillTooltip.CATALYST.tooltip(SBItems.HOLY_SHARD.get())
        );
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(BLOSSOM_ID, 0);
    }

    private void setBlossom(HealingBlossom blossom) {
        this.spellData.set(BLOSSOM_ID, blossom.getId());
    }


    private HealingBlossom getBlossom(SpellContext context) {
        Entity entity = context.getLevel().getEntity(this.spellData.get(BLOSSOM_ID));
        return (entity instanceof HealingBlossom blossom) ? blossom : null;
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            HealingBlossom blossom = this.summonEntity(context, SBEntities.HEALING_BLOSSOM.get(), healingBlossom -> {
                if (context.hasSkill(SBSkills.BLOOM)) {
                    healingBlossom.fastBloom();
                    this.fastBloomed = true;
                }

                if (context.hasSkill(SBSkills.REBIRTH) && context.hasCatalyst(SBItems.HOLY_SHARD.get())) {
                    context.useCatalyst(SBItems.HOLY_SHARD.get());
                    healingBlossom.setEmpowered(true);
                }

                this.setBlossom(healingBlossom);

                //Sound
                level.playSound(null, healingBlossom.blockPosition(), SoundEvents.CHORUS_FLOWER_GROW,
                        healingBlossom.getSoundSource(), 0.9F, 0.7F);
            });

            if (context.hasSkill(SBSkills.REBIRTH)) {
                this.addEventBuff(
                        caster,
                        SBSkills.REBIRTH,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.DEATH,
                        REBIRTH,
                        this::onPlayerDeath);
            }

            if (context.hasSkill(SBSkills.PETAL_SHIELD)) {
                this.addSkillBuff(
                        caster,
                        SBSkills.PETAL_SHIELD,
                        PETAL_SHIELD,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.ARMOR, new AttributeModifier(PETAL_SHIELD, potency(0.2F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                );
            }

            if (context.hasSkill(SBSkills.VERDANT_RENEWAL)) {
                this.cleanseCaster();
                this.addEventBuff(
                        caster,
                        SBSkills.VERDANT_RENEWAL,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.EFFECT_APPLICABLE,
                        VERDANT_RENEWAL,
                        event -> {
                            if (blossom != null && caster.distanceToSqr(blossom) < 2.5 * 2.5 && !event.getEffectInstance().getEffect().value().isBeneficial())
                                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                        }
                );
            }
            //VFX
            this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("healing_blossom_area"),
                    blossom.getId(), EntityEffectExecutor.AutoRotate.NONE));
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        if ((tickCount -8) % 31 != 0 && tickCount % 31 != 0 && tickCount % 20 != 0) return;
        HealingBlossom blossom = getBlossom(context);
        if (blossom == null)
            return;

        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (context.hasSkill(SBSkills.HEALING_WINDS)) {
            float distance = caster.distanceTo(blossom);
            if (distance > 20) {
                blossom.teleportToAroundBlockPos(caster.blockPosition().above());
            } else if (distance > 7) {
                Vec3 towardsCaster = caster.position().subtract(blossom.position()).normalize().scale(0.3f);
                blossom.setDeltaMovement(towardsCaster);
            } else if (level.getBlockState(blossom.blockPosition().below(2)).is(Blocks.AIR)){
                double grav = blossom.getGravity() == 0 ? 0 : blossom.getGravity() * -1;
                blossom.setDeltaMovement(0, grav, 0);
            } else {
                blossom.setDeltaMovement(Vec3.ZERO);
            }
        }

        if (!level.isClientSide) {
            if (!this.hasBursted && context.hasSkill(SBSkills.BURST_OF_LIFE)) {
                this.heal(caster, 4F);
                this.hasBursted = true;
            }

            List<LivingEntity> attackableEntities = this.getAttackableEntities(blossom, 2.5);
            if (context.hasSkill(SBSkills.THORNY_VINES) && (tickCount - 8) % 31 == 0) {
                for (LivingEntity entity : attackableEntities) {
                    this.hurt(entity, 4F);
                }
            } else if (context.hasSkill(SBSkills.THORNY_VINES) && tickCount % 31 == 0 && !attackableEntities.isEmpty()) {
                blossom.triggerAnim("actionController", "attack");
            }

            float healingAmount = 4F;
            List<LivingEntity> alliedEntities = this.getAlliedEntities(blossom, potency(2.5F));
            for (LivingEntity entity : alliedEntities) {
                if (entity.is(caster)) {
//                    if (context.hasSkill(SBSkills.VERDANT_RENEWAL))
//                        this.cleanseCaster();

                    if (context.hasSkill(SBSkills.FLOURISHING_GROWTH)) {
                        float maxHp = caster.getMaxHealth();
                        float currentHp = caster.getHealth();
                        float overflowHp = (currentHp + healingAmount) - maxHp;

                        if (overflowHp > 0) {
                            this.giveMana(caster, overflowHp * potency(1.5F));
                        }
                    }

                    this.heal(caster, healingAmount);
                } else if (context.hasSkill(SBSkills.FLOWER_FIELD)) {
                    this.heal(entity, healingAmount / 2.0F);
                }
            }
            this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("healing_blossom_area"),
                            this.getId(), EntityEffectExecutor.AutoRotate.NONE).setOffset(0, 0.1, 0));
        }
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return ((fastBloomed && tickCount >= 20) || tickCount >= 200);
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.removeSkillBuff(caster, SBSkills.PETAL_SHIELD);
            this.removeSkillBuff(caster, SBSkills.REBIRTH);
            HealingBlossom blossom = getBlossom(context);
            if (blossom != null)
                blossom.setEndTick(20);
        }
        removeSpellFX(CommonClass.customLocation("healing_blossom_area"));
    }

    private void onPlayerDeath(DeathEvent event) {
        LivingEntity caster = event.getCaster();
        HealingBlossom blossom = getBlossom(getCastContext());
        if (blossom == null || !blossom.isEmpowered())
            return;

        //VFX
        this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("healing_blossom_rebirth_flower"), blossom.getId(), EntityEffectExecutor.AutoRotate.NONE)
                .setOffset(0, 0.1, 0));
        this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("healing_blossom_rebirth_player"), caster.getId(), EntityEffectExecutor.AutoRotate.NONE)
                .setOffset(0, 1, 0));

        caster.setHealth(caster.getMaxHealth() * 0.5F);
        blossom.setEmpowered(false);
        if (caster instanceof Player player)
            shakeScreen(player);
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag nbt = super.saveData(compoundTag);
        nbt.putBoolean("FastBloomed", fastBloomed);
        nbt.putBoolean("HasBursted", hasBursted);
        return nbt;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        this.fastBloomed = nbt.getBoolean("FastBloomed");
        this.hasBursted = nbt.getBoolean("HasBursted");
    }
}