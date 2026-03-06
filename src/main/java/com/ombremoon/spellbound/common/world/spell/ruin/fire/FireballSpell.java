package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.gui.SkillTooltipProvider;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.spell.Fireball;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FireballSpell extends AnimatedSpell implements RadialSpell, ChargeableSpell {
    public static Builder<FireballSpell> createFireballBuilder() {
        return createSimpleSpellBuilder(FireballSpell.class)
                .manaCost(30)
                .baseDamage(3)
                .castCondition((context, fireballSpell) -> {
                    fireballSpell.choice = context.getChoice();
                    if (fireballSpell.isChoice(SBSkills.HOMING_MISSILE)) {
                        return context.hasSkill(SBSkills.AUTO_TARGETING) || context.getTarget() instanceof LivingEntity;
                    }

                    return true;
                });
    }
    private Skill choice;
    private float fireballRange;
    private int pierceCount;

    public FireballSpell() {
        super(SBSpells.FIREBALL.get(), createFireballBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.FIREBALL,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage())),
                SkillTooltip.EXPLOSION_RADIUS.tooltip(2.0F),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.RAPID_FIRE,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage(this.getBaseDamage() / 2))),
                SkillTooltip.EXPLOSION_RADIUS.tooltip(1.0F),
                SkillTooltip.RADIUS.tooltip(1.0F),
                SkillTooltip.PROJECTILE_COUNT.tooltip(3),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.STICKY_BOMB,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage())),
                SkillTooltip.EXPLOSION_RADIUS.tooltip(2.0F),
                SkillTooltip.DURATION.tooltip(60),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.CHARGED_BLAST,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage())),
                SkillTooltip.EXPLOSION_RADIUS.tooltip(2.0F),
                SkillTooltip.FLAT_DAMAGE_PER_CHARGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, 1.0F)),
                SkillTooltip.FLAT_RADIUS_PER_CHARGE.tooltip(1.0F),
                SkillTooltip.MAX_CHARGES.tooltip(3),
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.VOLATILE_CLUSTER,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage(this.getBaseDamage() / 2))),
                SkillTooltip.EXPLOSION_RADIUS.tooltip(1.0F),
                SkillTooltip.PROJECTILE_COUNT_PER_CHARGE.tooltip(1),
                SkillTooltip.MAX_CHARGES.tooltip(3),
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.HOMING_MISSILE,
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.EXPLOSIVE_AMPLIFIER,
                SkillTooltip.MODIFY_RADIUS.tooltip(20F)
        );
        this.addSkillDetails(SBSkills.BURNING_ADHESIVE,
                DAMAGE_TO_ARMOR.tooltip(5F)
        );
        this.addSkillDetails(SBSkills.MOLTEN_CORE,
                DAMAGE_PER_PIERCE.tooltip(-25F)
        );
        this.addSkillDetails(SBSkills.CLUSTER_STRIKE,
                SkillTooltip.MAX_CHARGES.tooltip(5)
        );
        this.addSkillDetails(SBSkills.AUTO_TARGETING,
                SkillTooltip.RADIUS.tooltip(10.0F)
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        if (!level.isClientSide) {
            this.fireballRange = getFireballRange(context);
            int count = this.isChoice(SBSkills.VOLATILE_CLUSTER) ? this.getCharges() : 1;
            for (int i = 0; i < count; i++) {
                float yAngle = getFireballAngle(caster, i, count);
                this.shootProjectile(context, SBEntities.FIREBALL.get(), caster.getXRot(), yAngle, 1.5F, 1.0F, projectile -> {
                    if (this.isChoice(SBSkills.HOMING_MISSILE)) {
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

                    if (this.isChoice(SBSkills.STICKY_BOMB)) {
                        projectile.setSticky(true);
                    }

                    if (this.isChoice(SBSkills.CHARGED_BLAST)) {
                        projectile.setSize(this.getCharges());
                    } else  if (this.isChoice(SBSkills.RAPID_FIRE)) {
                        projectile.setSize(0.75F);
                    }
                });
            }
            //sound
           playCastSound(level, context);
        }
    }

    public void playCastSound(Level level, SpellContext context){
        float volume = 0.2F + level.random.nextFloat() * 0.2F;
        float pitch = 1.0F + level.random.nextFloat() * 0.2F;
        level.playSound(null, context.getCaster().blockPosition(), SpellboundSounds.FIREBALL_USE.get(),
                SoundSource.PLAYERS, volume, pitch);
        level.playSound(null,context.getCaster().blockPosition(), SpellboundSounds.FIREBALL_TRAVEL.get(),
                SoundSource.PLAYERS, volume, pitch);
        level.playSound(null, context.getCaster().blockPosition(), SpellboundSounds.FLAMEJET_USE.get(),
                SoundSource.PLAYERS, volume*0.7F, pitch*0.7F);
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide && this.isChoice(SBSkills.RAPID_FIRE) && this.tickCount % 2 == 1) {
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
        var list = this.getAttackableEntities(fireball, this.fireballRange + fireball.getSize());
        float damage = fireball.getSize() >= 1.0F ? this.getBaseDamage() + fireball.getSize() : this.getBaseDamage() / 2;
        damage *= (float) Math.pow(0.75F, this.pierceCount);
        for (LivingEntity entity : list) {
            if (this.hurt(entity, damage)) {
                if (context.hasSkill(SBSkills.BURNING_ADHESIVE) && fireball.isSticky() && fireball.getHomingTarget() == entity) {
                    for (ItemStack stack : entity.getArmorSlots()) {
                        if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armorItem) {
                            stack.hurtAndBreak(5, entity, armorItem.getEquipmentSlot());
                        }
                    }
                }
            }
        }

        if (discard) {
            fireball.discard();
        } else {
            this.pierceCount++;
        }
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
        if (this.isChoice(SBSkills.RAPID_FIRE) || this.isChoice(SBSkills.VOLATILE_CLUSTER)) {
            range = 1.0F;
        } else if (this.isChoice(SBSkills.CHARGED_BLAST)) {
            range += this.getCharges();
        }

        if (context.hasSkill(SBSkills.EXPLOSIVE_AMPLIFIER)) {
            range *= 1.2F;
        }

        return range;
    }

    @Override
    public int maxCharges(SpellContext context) {
        return this.isChoice(SBSkills.CHARGED_BLAST) ? 3 : context.hasSkill(SBSkills.CLUSTER_STRIKE) ? 5 : 3;
    }

    @Override
    protected int getDuration(SpellContext context) {
        return this.isChoice(SBSkills.RAPID_FIRE) ? 6 : super.getDuration(context);
    }

    @Override
    public int getCastTime(SpellContext context) {
        if (this.canCharge(context)) {
            return this.isChoice(SBSkills.CHARGED_BLAST) ? 60 : context.hasSkill(SBSkills.CLUSTER_STRIKE) ? 50 : 30;
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

    public static SkillTooltip<Float> DAMAGE_TO_ARMOR = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.burning_adhesive", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> DAMAGE_PER_PIERCE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.molten_core", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };
}
