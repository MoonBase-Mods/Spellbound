package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.entity.spell.HealingBlossom;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.buff.events.DamageEvent;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class HealingBlossomSpell extends AnimatedSpell {
    private static final SpellDataKey<Integer> BLOSSOM_ID = SyncedSpellData.registerDataKey(HealingBlossomSpell.class, SBDataTypes.INT.get());
    private static final ResourceLocation PETAL_SHIELD = CommonClass.customLocation("petal_shield");
    private static final ResourceLocation PLAYER_DAMAGE = CommonClass.customLocation("rebirth_damage");
    private boolean fastBloomed = false;
    private boolean hasBursted = false;

    private static Builder<HealingBlossomSpell> createHealingBlossomSpell() {
        return createSimpleSpellBuilder(HealingBlossomSpell.class)
                .mastery(SpellMastery.EXPERT)
                .manaCost(45)
                .duration(400)
                .castTime(20)
                .castCondition((context, spell) -> spell.hasValidSpawnPos())
                .fullRecast();
    }

    public HealingBlossomSpell() {
        super(SBSpells.HEALING_BLOSSOM.get(), createHealingBlossomSpell());
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
        if (context.getLevel().isClientSide) return;
        SkillHolder skills = context.getSkills();
        this.summonEntity(context, SBEntities.HEALING_BLOSSOM.get(), SpellUtil.getCastRange(context.getCaster()), blossom -> {
            if (skills.hasSkill(SBSkills.BLOOM)) {
                blossom.fastBloom();
                this.fastBloomed = true;
            }
            if (skills.hasSkill(SBSkills.REBIRTH) && context.hasCatalyst(SBItems.HOLY_SHARD.get())) {
                context.useCatalyst(SBItems.HOLY_SHARD.get());
                blossom.setEmpowered(true);
            }
            setBlossom(blossom);
        });

        addEventBuff(context.getCaster(),
                SBSkills.REBIRTH,
                BuffCategory.BENEFICIAL,
                SpellEventListener.Events.PRE_DAMAGE,
                PLAYER_DAMAGE,
                this::onDamagePre);

        if (skills.hasSkill(SBSkills.PETAL_SHIELD))
            this.addSkillBuff(
                context.getCaster(),
                SBSkills.PETAL_SHIELD,
                BuffCategory.BENEFICIAL,
                SkillBuff.ATTRIBUTE_MODIFIER,
                new ModifierData(Attributes.ARMOR, new AttributeModifier(PETAL_SHIELD,
                        1.2f,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                this.getDuration()
        );
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        if ((tickCount -8) % 31 != 0 && tickCount % 31 != 0 && tickCount % 20 != 0) return;
        HealingBlossom blossom = getBlossom(context);
        if (blossom == null) return;
        LivingEntity caster = context.getCaster();
        SkillHolder skills = context.getSkills();
        Level level = context.getLevel();

        if (skills.hasSkill(SBSkills.HEALING_WINDS)) {
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
        if (context.getLevel().isClientSide) return;

        if (!this.hasBursted && skills.hasSkill(SBSkills.BURST_OF_LIFE)) {
            context.getCaster().heal(4f);
            this.hasBursted = true;
        }

        //Damage is done separately to healing to sync with animation better
        List<LivingEntity> effectedEntities = level.getEntitiesOfClass(LivingEntity.class, blossom.getBoundingBox().inflate(5));
        if (skills.hasSkill(SBSkills.THORNY_VINES) && (tickCount -8) % 31 == 0) {
            for (LivingEntity entity : effectedEntities) {
                if (canAttack(entity))
                    this.hurt(blossom, entity, 4f);
            }
        } else if (skills.hasSkill(SBSkills.THORNY_VINES) && tickCount % 31 == 0) {
            for (LivingEntity entity : effectedEntities) {
                if (canAttack(entity)) {
                    blossom.triggerAnim("actionController", "attack");
                    break;
                }
            }
        }

        float healingAmount = 2f;
        for (LivingEntity entity : effectedEntities) {
            if (entity.is(caster)) {
                if (skills.hasSkill(SBSkills.VERDANT_RENEWAL))
                    this.cleanseCaster();

                if (skills.hasSkill(SBSkills.FLOURISHING_GROWTH)) {
                    float maxHp = caster.getMaxHealth();
                    float currentHp = caster.getHealth();
                    float overflowHp = (currentHp + healingAmount) - maxHp;

                    if (overflowHp > 0) {
                        context.getSpellHandler().awardMana(overflowHp * 5.0F);
                    }
                }
                this.heal(caster, healingAmount);
            } else if (entity instanceof Player && skills.hasSkill(SBSkills.FLOWER_FIELD)) {
                this.heal(entity, healingAmount / 2.0F);
            }
        }
    }

    private boolean canAttack(LivingEntity entity) {
        return entity instanceof Mob mob
                /*&& !mob.isAlliedTo(getCastContext().getCaster())*/;
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return ((fastBloomed && tickCount >= 20) || tickCount >= 200);
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        if (!context.getLevel().isClientSide) {
            HealingBlossom blossom = getBlossom(context);
            if (blossom != null)
                blossom.setEndTick(20);
        }
    }

    private void onDamagePre(DamageEvent.Pre pre) {
        /*HealingBlossom blossom = getBlossom(getCastContext());
        if (blossom == null) return;
        if (!blossom.isEmpowered()) return;

        float currentHp = pre.getCaster().getHealth();
        if (currentHp - pre.getNewDamage() <= 0) {
            pre.setNewDamage(0f);
            pre.getCaster().setHealth(pre.getCaster().getMaxHealth()/2f);
            blossom.setEmpowered(false);
            if (pre.getCaster() instanceof Player player)
                shakeScreen(player); //TODO: Sort intensity and add revive anim
        }*/
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("FastBloomed", fastBloomed);
        compoundTag.putBoolean("HasBursted", hasBursted);
        return compoundTag;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        this.fastBloomed = nbt.getBoolean("FastBloomed");
        this.hasBursted = nbt.getBoolean("HasBursted");
    }
}