package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;

public class PurgeMagicSpell extends AnimatedSpell implements RadialSpell {
    private static final ResourceLocation DOMINANT_MAGIC = CommonClass.customLocation("dominant_magic");
    private static final ResourceLocation RESIDUAL_DISRUPTION = CommonClass.customLocation("residual_disruption");
    private static final ResourceLocation UNFOCUSED = CommonClass.customLocation("unfocused");
    private static final ResourceLocation MANA_SPONGE = CommonClass.customLocation("mana_sponge");

    private static Builder<PurgeMagicSpell> createPurgeMagicBuilder() {
        return createSimpleSpellBuilder(PurgeMagicSpell.class)
                .duration(10)
                .manaCost(27)
                .castCondition((context, purgeMagicSpell) -> {
                    if (context.isChoice(SBSkills.PURGE_MAGIC))
                        return context.hasSkill(SBSkills.RADIO_WAVES) || context.getTarget() instanceof LivingEntity;
                    return true;
                })
                .fullRecast(true);
    }

    public PurgeMagicSpell() {
        super(SBSpells.PURGE_MAGIC.get(), createPurgeMagicBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.PURGE_MAGIC,
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.RADIO_WAVES,
                SkillTooltip.RADIUS.tooltip(potency(3)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.COUNTER_MAGIC,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.COUNTER_MAGIC),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.MANA_SPONGE,
                SkillTooltip.MANA_ABSORB.tooltip(potency(75F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.DOMINANT_MAGIC,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.SILENCED),
                SkillTooltip.EFFECT_DURATION.tooltip(100)
        );
        this.addSkillDetails(SBSkills.RESIDUAL_DISRUPTION,
                SkillTooltip.TARGET_CAST_CHANCE.tooltip(-50F),
                SkillTooltip.EFFECT_DURATION.tooltip(100)
        );
        this.addSkillDetails(SBSkills.UNFOCUSED,
                SkillTooltip.TARGET_SPELL_POTENCY.tooltip(-10F),
                SkillTooltip.EFFECT_DURATION.tooltip(140)
        );
        this.addSkillDetails(SBSkills.MAGIC_POISONING,
                SkillTooltip.MANA_DRAIN.tooltip(potency(10F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.EXPUNGE,
                SkillTooltip.COOLDOWN.tooltip(24000),
                SkillTooltip.CATALYST.tooltip(SBItems.FOOL_SHARD.get())
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (context.isChoice(SBSkills.COUNTER_MAGIC)) {
                caster.addEffect(new MobEffectInstance(SBEffects.COUNTER_MAGIC, 200, 0, false, false));
                if (context.hasSkill(SBSkills.CLEANSE)) {
                    this.cleanseCaster();
                }

                if (context.hasSkill(SBSkills.MANA_SPONGE)) {
                    this.addEventBuff(
                            caster,
                            SBSkills.MANA_SPONGE,
                            BuffCategory.BENEFICIAL,
                            SpellEventListener.Events.POST_DAMAGE,
                            MANA_SPONGE,
                            post -> {
                                DamageSource source = post.getSource();
                                if (source instanceof SpellDamageSource damageSource) {
                                    AbstractSpell spell = damageSource.getSpell();
                                    if (spell != null) {
                                        this.giveMana(caster, spell.getManaCost() * potency(75F));
                                        this.removeSkillBuff(caster, SBSkills.MANA_SPONGE);
                                    }
                                }
                            },
                            200
                    );
                }
            } else {
                Set<LivingEntity> targets = new ObjectOpenHashSet<>();
                if (context.hasSkill(SBSkills.RADIO_WAVES)) {
                    targets.addAll(this.getAttackableEntities(potency(3)));
                } else {
                    targets.add((LivingEntity) context.getTarget());
                }

                for (LivingEntity target : targets) {
                    var targetHandler = SpellUtil.getSpellHandler(target);
                    var activeSpells = targetHandler.getActiveSpells();
                    this.cleanse(target, 0, MobEffectCategory.BENEFICIAL);
                    targetHandler.getBuffs().stream().filter(SkillBuff::isBeneficial).forEach(skillBuff -> removeSkillBuff(target, skillBuff.skill()));
                    for (AbstractSpell spell : activeSpells) {
                        spell.endSpell();
                    }

                    if (context.hasSkill(SBSkills.DOMINANT_MAGIC))
                        addSkillBuff(
                                target,
                                SBSkills.DOMINANT_MAGIC,
                                DOMINANT_MAGIC,
                                BuffCategory.HARMFUL,
                                SkillBuff.MOB_EFFECT,
                                new MobEffectInstance(SBEffects.SILENCED, 100, 0, false, false),
                                100
                        );

                    if (context.hasSkill(SBSkills.RESIDUAL_DISRUPTION)) {
                        addSkillBuff(
                                target,
                                SBSkills.RESIDUAL_DISRUPTION,
                                RESIDUAL_DISRUPTION,
                                BuffCategory.HARMFUL,
                                SkillBuff.SPELL_MODIFIER,
                                SpellModifier.RESIDUAL_DISRUPTION,
                                100
                        );
                    }

                    if (context.hasSkill(SBSkills.UNFOCUSED))
                        addSkillBuff(
                                target,
                                SBSkills.UNFOCUSED,
                                UNFOCUSED,
                                BuffCategory.HARMFUL,
                                SkillBuff.SPELL_MODIFIER,
                                SpellModifier.UNFOCUSED,
                                140
                        );

                    if (context.hasSkill(SBSkills.MAGIC_POISONING))
                        targetHandler.consumeMana(potency(10) * activeSpells.size(), true);

                    if (context.hasSkill(SBSkills.NULLIFICATION)) {
                        List<ItemStack> itemSlots = new ObjectArrayList<>();
                        target.getAllSlots().forEach(itemSlots::add);
                        itemSlots = itemSlots.stream().filter(ItemStack::isEnchanted).toList();
                        if (!itemSlots.isEmpty()) {
                            int randSlot = target.getRandom().nextInt(0, itemSlots.size());
                            ItemStack stack = itemSlots.get(randSlot);
                            var enchantments = stack.getAllEnchantments(target.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).keySet().stream().toList();
                            int randEnchant = target.getRandom().nextInt(0, enchantments.size());
                            stack.enchant(enchantments.get(randEnchant), 0);
                        }
                    }

                    var spellList = targetHandler.getSpellList();
                    if (context.hasSkillReady(SBSkills.EXPUNGE) && context.hasCatalyst(SBItems.FOOL_SHARD.get()) && !spellList.isEmpty()) {
                        int randSpell = target.getRandom().nextInt(0, spellList.size());
                        SpellType<?> spellType = targetHandler.getSpellList().stream().toList().get(randSpell);
                        targetHandler.removeSpell(spellType);
                        addCooldown(SBSkills.EXPUNGE, 24000);
                        context.useCatalyst(SBItems.FOOL_SHARD.get());
                    }
                }
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.isChoice(SBSkills.COUNTER_MAGIC) ? 1 : super.getDuration(context);
    }
}
