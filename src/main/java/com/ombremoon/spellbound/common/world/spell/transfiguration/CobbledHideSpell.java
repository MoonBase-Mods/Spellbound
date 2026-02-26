package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.gui.SkillTooltipProvider;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CobbledHideSpell extends AnimatedSpell {
    private static final ResourceLocation COBBLED_HIDE = CommonClass.customLocation("hide_spell_mod");
    private static final ResourceLocation IRON_HIDE = CommonClass.customLocation("iron_hide");
    private static final ResourceLocation DIAMOND_HIDE = CommonClass.customLocation("diamond_hide");
    private static final ResourceLocation DRAGON_HIDE = CommonClass.customLocation("dragon_hide");
    private static final ResourceLocation BEDROCK_BASTION = CommonClass.customLocation("bedrock_bastion");
    private static final ResourceLocation GRANITE_GRIP = CommonClass.customLocation("granite_grip");
    private static final ResourceLocation MASONRY_WARD = CommonClass.customLocation("masonry_ward");
    private static final ResourceLocation BOULDERBACK = CommonClass.customLocation("boulderback");
    private static final ResourceLocation INFUSED_STONE = CommonClass.customLocation("infused_stone");
    private static final ResourceLocation INFUSED_STONE_BUFF = CommonClass.customLocation("infused_stone_buff");
    private float armorBonus;
    private boolean canWard;
    private int wardTick;

    private static Builder<CobbledHideSpell> createCobbledHideBuilder() {
        return createSimpleSpellBuilder(CobbledHideSpell.class)
                .manaCost(30)
                .duration(1200)
                .selfBuffCast()
                .fullRecast(true);
    }

    public CobbledHideSpell() {
        super(SBSpells.COBBLED_HIDE.get(), createCobbledHideBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.COBBLED_HIDE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ARMOR, new AttributeModifier(COBBLED_HIDE, potency(5F), AttributeModifier.Operation.ADD_VALUE))),
                SkillTooltip.DURATION.tooltip(1200),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.IRON_HIDE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ARMOR, new AttributeModifier(IRON_HIDE, potency(8F), AttributeModifier.Operation.ADD_VALUE))),
                SkillTooltip.PROJECTILE_DAMAGE_REDUX.tooltip(potency(10F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.DIAMOND_HIDE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ARMOR, new AttributeModifier(DIAMOND_HIDE, potency(13F), AttributeModifier.Operation.ADD_VALUE))),
                SkillTooltip.SPELL_DAMAGE_REDUX.tooltip(potency(10F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.DRAGON_HIDE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ARMOR, new AttributeModifier(DRAGON_HIDE, potency(20F), AttributeModifier.Operation.ADD_VALUE))),
                SkillTooltip.MOB_EFFECT.tooltip(MobEffects.FIRE_RESISTANCE),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.BEDROCK_BASTION,
                SkillTooltip.HP_THRESHOLD.tooltip(20F),
                SkillTooltip.EFFECT_DURATION.tooltip(60),
                SkillTooltip.COOLDOWN.tooltip(24000)
        );
        this.addSkillDetails(SBSkills.SHATTER_SKIN,
                ARMOR_TO_DAMAGE.tooltip(75F),
                SkillTooltip.RADIUS.tooltip(4F),
                SkillTooltip.MODIFY_MANA_COST.tooltip(-50F)
        );
        this.addSkillDetails(SBSkills.STONE_WALL,
                SkillTooltip.MODIFY_DURATION.tooltip(50F)
        );
        this.addSkillDetails(SBSkills.GRANITE_GRIP,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(GRANITE_GRIP, 1.0F, AttributeModifier.Operation.ADD_VALUE)))
        );
        this.addSkillDetails(SBSkills.MASONRY_WARD,
                SkillTooltip.SPELL_DAMAGE_REDUX.tooltip(potency(25F)),
                SkillTooltip.PROC_DURATION.tooltip(120),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.BOULDERBACK,
                SkillTooltip.DAMAGE_REFLECTION.tooltip(potency(25F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.INFUSED_STONE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(SBAttributes.MANA_REGEN, new AttributeModifier(INFUSED_STONE, potency(0.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            float armorAmount = 5.0F;
            boolean isDragonHide = context.hasSkill(SBSkills.DRAGON_HIDE);
            boolean isDiamondHide = context.hasSkill(SBSkills.DIAMOND_HIDE);
            boolean isIronHide = context.hasSkill(SBSkills.IRON_HIDE);
            if (isDragonHide) {
                armorAmount = 20.0F;
            } else if (isDiamondHide) {
                armorAmount = 13.0F;
            } else if (isIronHide) {
                armorAmount = 8.0F;
            }

            this.armorBonus = potency(armorAmount);
            this.addSkillBuff(
                    caster,
                    SBSkills.COBBLED_HIDE,
                    COBBLED_HIDE,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(Attributes.ARMOR, new AttributeModifier(COBBLED_HIDE, potency(this.armorBonus), AttributeModifier.Operation.ADD_VALUE))
            );

            if (isIronHide) {
                this.addEventBuff(
                        caster,
                        SBSkills.IRON_HIDE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.PRE_DAMAGE,
                        IRON_HIDE,
                        pre -> {
                            if (pre.getSource().getEntity() instanceof Projectile) {
                                pre.setNewDamage(pre.getOriginalDamage() * invertedPotency(0.9F));
                            }
                        }
                );
            }

            if (isDiamondHide) {
                this.addEventBuff(
                        caster,
                        SBSkills.DIAMOND_HIDE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.PRE_DAMAGE,
                        DIAMOND_HIDE,
                        pre -> {
                            if (isSpellDamage(pre.getSource())) {
                                pre.setNewDamage(pre.getOriginalDamage() * invertedPotency(0.9F));
                            }
                        }
                );
            }

            if (isDragonHide) {
                this.addSkillBuff(
                        caster,
                        SBSkills.DRAGON_HIDE,
                        DRAGON_HIDE,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(MobEffects.FIRE_RESISTANCE, this.getDuration(), 0, false, false)
                );
            }

            if (context.hasSkill(SBSkills.GRANITE_GRIP)) {
                this.addSkillBuff(
                        caster,
                        SBSkills.GRANITE_GRIP,
                        GRANITE_GRIP,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(GRANITE_GRIP, 1.0F, AttributeModifier.Operation.ADD_VALUE))
                );
            }

            if (context.hasSkill(SBSkills.MASONRY_WARD)) {
                this.addEventBuff(
                        caster,
                        SBSkills.MASONRY_WARD,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.PRE_DAMAGE,
                        MASONRY_WARD,
                        pre -> {
                            if (this.canWard && isSpellDamage(pre.getSource())) {
                                pre.setNewDamage(pre.getOriginalDamage() * invertedPotency(0.75F));
                                this.wardTick = this.tickCount;
                                this.canWard = false;
                            }
                        }
                );
            }

            if (context.hasSkill(SBSkills.BOULDERBACK)) {
                this.addEventBuff(
                        caster,
                        SBSkills.BOULDERBACK,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.POST_DAMAGE,
                        BOULDERBACK,
                        post -> {
                            DamageSource source = post.getSource();
                            if (source.getEntity() instanceof LivingEntity attacker && source.isDirect() && !isSpellDamage(source)) {
                                this.hurt(attacker, post.getNewDamage() * potency(0.25F));
                            }
                        }
                );
            }

            if (context.hasSkill(SBSkills.INFUSED_STONE)) {
                this.addEventBuff(
                        caster,
                        SBSkills.INFUSED_STONE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.POST_DAMAGE,
                        INFUSED_STONE,
                        post -> {
                            DamageSource source = post.getSource();
                            if (isSpellDamage(source)) {
                                this.addSkillBuff(
                                        caster,
                                        SBSkills.INFUSED_STONE,
                                        INFUSED_STONE_BUFF,
                                        BuffCategory.BENEFICIAL,
                                        SkillBuff.ATTRIBUTE_MODIFIER,
                                        new ModifierData(Attributes.ARMOR, new AttributeModifier(INFUSED_STONE_BUFF, potency(0.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                        100
                                );
                            }
                        }
                );
            }
        }
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide && context.hasSkill(SBSkills.SHATTER_SKIN)) {
            var list = this.getAttackableEntities(4.0D);
            for (LivingEntity entity : list) {
                this.hurt(entity, this.armorBonus * potency(0.5F));
            }

            this.endSpell();
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (context.hasSkill(SBSkills.MASONRY_WARD) && !this.canWard && this.tickCount >= this.wardTick + 120) {
                this.canWard = true;
            }

            if (context.hasSkillReady(SBSkills.BEDROCK_BASTION) && caster.getHealth() <= caster.getMaxHealth() * 0.2F) {
                this.addEventBuff(
                        caster,
                        SBSkills.BEDROCK_BASTION,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.PRE_DAMAGE,
                        BEDROCK_BASTION,
                        pre -> {
                            caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.ANVIL_BREAK, caster.getSoundSource(), 1.0F, 1.0F);
                            if (this.getRemainingTime() > 100) {
                                this.setRemainingTicks(100);
                            }

                            pre.setNewDamage(0);
                            this.removeSkillBuff(caster, SBSkills.BEDROCK_BASTION);
                        },
                        60
                );

                this.addCooldown(SBSkills.BEDROCK_BASTION, 12000);
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.removeSkillBuff(caster, SBSkills.COBBLED_HIDE);
            this.removeSkillBuff(caster, SBSkills.IRON_HIDE);
            this.removeSkillBuff(caster, SBSkills.DIAMOND_HIDE);
            this.removeSkillBuff(caster, SBSkills.DRAGON_HIDE);
            this.removeSkillBuff(caster, SBSkills.GRANITE_GRIP);
            this.removeSkillBuff(caster, SBSkills.BEDROCK_BASTION);
            this.removeSkillBuff(caster, SBSkills.MASONRY_WARD);
            this.removeSkillBuff(caster, SBSkills.BOULDERBACK);
            this.removeSkillBuff(caster, SBSkills.INFUSED_STONE);
        }
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.hasSkill(SBSkills.STONE_WALL) ? 1800 : super.getDuration(context);
    }

    @Override
    public float getManaCost(SpellContext context) {
        return context.isChoice(SBSkills.SHATTER_SKIN) && context.isRecast() ? super.getManaCost(context) * 0.5F : super.getManaCost(context);
    }

    public static SkillTooltip<Float> ARMOR_TO_DAMAGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.shatter_skin", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };
}
