package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.spell.Fireball;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FireballSpell extends AnimatedSpell implements RadialSpell, ChargeableSpell {
    public static Builder<FireballSpell> createFireballBuilder() {
        return createSimpleSpellBuilder(FireballSpell.class)
                .manaCost(30)
                .baseDamage(3)
                .castCondition((context, fireballSpell) -> {
                    if (context.isChoice(SBSkills.HOMING_MISSILE)) {
                        return context.hasSkill(SBSkills.AUTO_TARGETING) || context.getTarget() instanceof LivingEntity;
                    }

                    return true;
                });
    }
    private float fireballRange;

    public FireballSpell() {
        super(SBSpells.FIREBALL.get(), createFireballBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        if (!level.isClientSide) {
            this.fireballRange = getFireballRange(context);
            int count = context.isChoice(SBSkills.VOLATILE_CLUSTER) ? this.getCharges() : 1;
            for (int i = 0; i < count; i++) {
                float yAngle = getFireballAngle(caster, i, count);
                this.shootProjectile(context, SBEntities.FIREBALL.get(), caster.getXRot(), yAngle, 1.5F, 1.0F, projectile -> {
                    if (context.isChoice(SBSkills.HOMING_MISSILE)) {
                        if (context.getTarget() instanceof LivingEntity target) {
                            projectile.setHomingTarget(target);
                        } else if (context.hasSkill(SBSkills.AUTO_TARGETING)) {
                            var list = this.getAttackableEntities(projectile, 10.0D);
                            if (!list.isEmpty()) {
                                LivingEntity target = list.getFirst();
                                for (LivingEntity entity : list) {
                                    if (entity.distanceToSqr(caster) < target.distanceToSqr(caster))
                                        target = entity;
                                }

                                projectile.setHomingTarget(target);
                            }
                        }
                    }

                    if (context.isChoice(SBSkills.STICKY_BOMB)) {
                        projectile.setSticky(true);
                    }

                    if (context.isChoice(SBSkills.CHARGED_BLAST)) {
                        projectile.setSize(this.getCharges());
                    }
                });
            }
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide && context.isChoice(SBSkills.RAPID_FIRE) && this.tickCount % 2 == 1) {
            this.shootProjectile(context, SBEntities.FIREBALL.get(), 2.5F, 1.0F);
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    public void onProjectileHitEntity(ISpellEntity<?> spellEntity, SpellContext context, EntityHitResult result) {
        Level level = context.getLevel();
        if (!level.isClientSide && spellEntity instanceof Fireball fireball && result.getEntity() instanceof LivingEntity livingEntity) {
            if (fireball.isSticky()) {
                livingEntity.setData(SBData.STICKY_BOMB_LOCATION, result.getLocation());
                fireball.setStickTarget(livingEntity);
                fireball.setStickTime(fireball.tickCount + 60);
            } else {
                this.explode(context, fireball, !context.hasSkill(SBSkills.MOLTEN_CORE) || this.getCharges() < this.maxCharges(context));
            }
        }
    }

    @Override
    public void onProjectileHitBlock(ISpellEntity<?> spellEntity, SpellContext context, BlockHitResult result) {
        if (!context.getLevel().isClientSide && spellEntity instanceof Fireball fireball) {
            if (fireball.isSticky()) {
                fireball.setStickTime(fireball.tickCount + 60);
            } else {
                this.explode(context, fireball);
            }
        }
    }

    public void explode(Fireball fireball) {
        this.explode(this.getContext(), fireball, true);
    }

    public void explode(SpellContext context, Fireball fireball) {
        this.explode(context, fireball, true);
    }

    public void explode(SpellContext context, Fireball fireball, boolean discard) {
        var list = this.getAttackableEntities(fireball, this.getFireballRange(context) + fireball.getSize());
        for (LivingEntity entity : list) {
            if (this.hurt(entity, this.getBaseDamage() + fireball.getSize())) {
                if (context.hasSkill(SBSkills.BURNING_ADHESIVE) && fireball.isSticky() && fireball.getHomingTarget() == entity) {
                    for (ItemStack stack : entity.getArmorSlots()) {
                        if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armorItem) {
                            stack.hurtAndBreak(5, entity, armorItem.getEquipmentSlot());
                        }
                    }
                }
            }
        }

        if (discard)
            fireball.discard();
    }

    private float getFireballAngle(LivingEntity caster, int i, int count) {
        float yRot = caster.getYRot();
        float spread = 15.0F;
        float totalSpread = spread * (count - 1);
        float startOffset = -totalSpread / 2.0F;
        return yRot + startOffset + (i * spread);
    }

    private float getFireballRange(SpellContext context) {
        float range = 2.0F;
        if (context.isChoice(SBSkills.RAPID_FIRE) || context.isChoice(SBSkills.VOLATILE_CLUSTER)) {
            range = 1.0F;
        } else if (context.isChoice(SBSkills.CHARGED_BLAST)) {
            range += this.getCharges();
        }

        if (context.hasSkill(SBSkills.EXPLOSIVE_AMPLIFIER)) {
            range *= 1.2F;
        }

        return range;
    }

    @Override
    public int maxCharges(SpellContext context) {
        return context.isChoice(SBSkills.CHARGED_BLAST) ? 3 : context.hasSkill(SBSkills.CLUSTER_STRIKE) ? 5 : 3;
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.isChoice(SBSkills.RAPID_FIRE) ? 6 : super.getDuration(context);
    }

    @Override
    public int getCastTime(SpellContext context) {
        if (this.canCharge(context)) {
            return context.isChoice(SBSkills.CHARGED_BLAST) ? 60 : context.hasSkill(SBSkills.CLUSTER_STRIKE) ? 50 : 30;
        }

        return super.getCastTime(context);
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return context.isChoice(SBSkills.CHARGED_BLAST) || context.isChoice(SBSkills.VOLATILE_CLUSTER);
    }

    @Override
    public boolean shouldRender(SpellContext context) {
        return false;
    }
}
