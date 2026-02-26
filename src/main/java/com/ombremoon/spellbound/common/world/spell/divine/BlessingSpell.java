package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.gui.SkillTooltipProvider;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.world.EntityResource;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlessingSpell extends AnimatedSpell {
    private static final ResourceLocation COURAGE = CommonClass.customLocation("courage");
    private static final ResourceLocation OVERFLOWING_AID = CommonClass.customLocation("overflowing_aid");
    private static final ResourceLocation UPLIFTING_CHORUS = CommonClass.customLocation("uplifting_chorus");

    public static Builder<BlessingSpell> createBlessingBuilder() {
        return createSimpleSpellBuilder(BlessingSpell.class)
                .duration(100)
                .manaCost(35)
                .castCondition((context, spell) -> {
                    Entity entity = context.getTarget();
                   if (entity instanceof LivingEntity livingEntity) {
                       spell.targets.add(livingEntity);
                       return true;
                   }

                   return false;
                });
    }
    private final Set<LivingEntity> targets = new ObjectOpenHashSet<>();
    private EntityResource resource;
    private float amount;

    public BlessingSpell() {
        super(SBSpells.BLESSING.get(), createBlessingBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.BLESSING,
                SkillTooltip.HEAL.tooltip(this.getModifiedHeal(this.getJudgementFactor())),
                SkillTooltip.DURATION.tooltip(100),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.COURAGE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ARMOR, new AttributeModifier(COURAGE, potency(5.0F * this.getJudgementFactor()), AttributeModifier.Operation.ADD_VALUE))),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.ARCANE_RESTORATION,
                SkillTooltip.MANA.tooltip(potency(5.0F * this.getJudgementFactor())),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.SATIATING_BLESSING,
                SkillTooltip.HUNGER.tooltip((int) potency(this.getJudgementFactor())),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.AIR_BUBBLE,
                AIR.tooltip(potency(20 * this.getJudgementFactor())),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.EXTENDED_GRACE,
                SkillTooltip.MODIFY_DURATION.tooltip(100F),
                RESOURCE.tooltip(50F)
        );
        this.addSkillDetails(SBSkills.SHARED_BOON,
                SkillTooltip.ALLY_RANGE.tooltip(4.0F)
        );
        this.addSkillDetails(SBSkills.OVERFLOWING_AID,
                SkillTooltip.DAMAGE_REDUX.tooltip(potency(10F))
        );
        this.addSkillDetails(SBSkills.CONSECRATED_PRESENCE,
                SkillTooltip.ALLY_RANGE.tooltip(5.0F)
        );
        this.addSkillDetails(SBSkills.UPLIFTING_CHORUS,
                SkillTooltip.MAX_HEALTH_HEAL.tooltip(30F),
                SkillTooltip.CATALYST.tooltip(SBItems.HOLY_SHARD.get())
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            EntityResource resource = null;
            float amount = 1.0F;
            if (context.hasSkill(SBSkills.SHARED_BOON)) {
                var list = this.getAlliedEntities(4.0F);
                if (!list.isEmpty()) {
                    for (LivingEntity entity : list) {
                        if (!this.targets.contains(entity)) {
                            this.targets.add(entity);
                            break;
                        }
                    }
                }
            }

            if (context.isChoice(SBSkills.BLESSING)) {
                resource = EntityResource.HEALTH;
            } else if (context.isChoice(SBSkills.COURAGE)) {
                for (LivingEntity entity : this.targets) {
                    this.addSkillBuff(
                            entity,
                            SBSkills.COURAGE,
                            COURAGE,
                            BuffCategory.BENEFICIAL,
                            SkillBuff.ATTRIBUTE_MODIFIER,
                            new ModifierData(Attributes.ARMOR, new AttributeModifier(COURAGE, 5.0F, AttributeModifier.Operation.ADD_VALUE)),
                            this.getDuration(context)
                    );
                }

                this.endSpell();
                return;
            } else if (context.isChoice(SBSkills.ARCANE_RESTORATION)) {
                resource = EntityResource.MANA;
                amount = 5.0F;
            } else if (context.isChoice(SBSkills.SATIATING_BLESSING)) {
                resource = EntityResource.HUNGER;
            } else if (context.isChoice(SBSkills.AIR_BUBBLE)) {
                resource = EntityResource.AIR;
                amount = 20.0F;
            } else if (context.isChoice(SBSkills.PURIFYING_WARD)) {
                for (LivingEntity entity : this.targets) {
                    this.cleanse(entity, 1, MobEffectCategory.HARMFUL);
                }

                this.endSpell();
                return;
            }

            boolean extended = context.hasSkill(SBSkills.EXTENDED_GRACE);
            this.resource = resource;
            float modifier = extended ? 1.5F : 1.0F;
            float judgementFactor = this.getJudgementFactor();
            this.amount = potency(amount * modifier * judgementFactor);

            if (context.hasSkill(SBSkills.UPLIFTING_CHORUS) && context.hasCatalyst(SBItems.HOLY_SHARD.get())) {
                for (LivingEntity entity : this.targets) {
                    this.addEventBuff(
                            entity,
                            SBSkills.UPLIFTING_CHORUS,
                            BuffCategory.BENEFICIAL,
                            SpellEventListener.Events.DEATH,
                            UPLIFTING_CHORUS,
                            deathEvent -> {
                                LivingEntity killedEntity = deathEvent.getKilledEntity();
                                float health = killedEntity.getMaxHealth() * 0.3F;
                                killedEntity.setHealth(health);
                                caster.hurt(SpellUtil.spellDamageSource(caster.level(), SBDamageTypes.SB_GENERIC, this, caster, caster), health * 0.5F);
                                this.removeSkillBuff(killedEntity, SBSkills.UPLIFTING_CHORUS);
                                deathEvent.cancelEvent();
                            },
                            this.getDuration(context)
                    );
                }
            }
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide) {
            List<LivingEntity> toRemove = new ArrayList<>();
            for (LivingEntity entity : this.targets) {
                if (entity != null && entity.isAlive()) {
                    if (this.resource != null) {
                        if (context.hasSkill(SBSkills.OVERFLOWING_AID) && this.resource.isFull(entity)) {
                            this.addEventBuff(
                                    entity,
                                    SBSkills.OVERFLOWING_AID,
                                    BuffCategory.BENEFICIAL,
                                    SpellEventListener.Events.PRE_DAMAGE,
                                    OVERFLOWING_AID,
                                    pre -> pre.setNewDamage(pre.getOriginalDamage() * invertedPotency(0.9F))
                            );
                            return;
                        }

                        this.resource.consume(entity, this.amount, EntityResource.Operation.ADD);

                        if (context.hasSkill(SBSkills.CONSECRATED_PRESENCE)) {
                            List<LivingEntity> nearbyEntities = this.getAlliedEntities(entity, 5.0F);
                            for (LivingEntity nearbyAlly : nearbyEntities) {
                                if (!this.targets.contains(nearbyAlly) && !this.isCaster(nearbyAlly))
                                    this.resource.consume(nearbyAlly, this.amount * 0.5F, EntityResource.Operation.ADD);
                            }
                        }
                    }
                } else {
                    toRemove.add(entity);
                }
            }

            toRemove.forEach(this.targets::remove);
            if (this.targets.isEmpty())
                this.endSpell();
        }
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return !this.targets.isEmpty() && this.tickCount % 20 == 0;
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide && !this.targets.isEmpty()) {
            for (LivingEntity entity : this.targets) {
                if (entity != null && entity.isAlive()) {
                    this.removeSkillBuffs(entity, SBSkills.COURAGE, SBSkills.OVERFLOWING_AID, SBSkills.UPLIFTING_CHORUS);
                }
            }
        }
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.hasSkill(SBSkills.EXTENDED_GRACE) ? 200 : super.getDuration(context);
    }

    public static SkillTooltip<Float> AIR = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.air_bubble", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> RESOURCE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.extended_grace", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };
}
