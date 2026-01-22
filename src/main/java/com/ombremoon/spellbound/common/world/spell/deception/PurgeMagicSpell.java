package com.ombremoon.spellbound.common.world.spell.deception;

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
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class PurgeMagicSpell extends AnimatedSpell implements RadialSpell {
    private static Builder<PurgeMagicSpell> createPurgeMagicBuilder() {
        return createSimpleSpellBuilder(PurgeMagicSpell.class)
                .duration(10)
                .manaCost(27)
                .castCondition((context, purgeMagicSpell) -> {
                    if (context.isChoice(SBSkills.PURGE_MAGIC))
                        return context.getSkills().hasSkill(SBSkills.RADIO_WAVES) || context.getTarget() instanceof LivingEntity;
                    return true;
                })
                .fullRecast();
    }

    public PurgeMagicSpell() {
        super(SBSpells.PURGE_MAGIC.get(), createPurgeMagicBuilder());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        var skills = context.getSkills();
        if (!level.isClientSide) {
            if (context.isChoice(SBSkills.COUNTER_MAGIC)) {
                caster.addEffect(new MobEffectInstance(SBEffects.COUNTER_MAGIC, 200, 0, false, false));
                if (skills.hasSkill(SBSkills.CLEANSE.value()))
                    this.cleanseCaster();
            } else {
                List<LivingEntity> targets = new ObjectArrayList<>();
                if (skills.hasSkill(SBSkills.RADIO_WAVES.value())) {
                    targets.addAll(level.getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(3), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                } else {
                    targets.add((LivingEntity) context.getTarget());
                }

                for (LivingEntity target : targets) {
                    var targetHandler = SpellUtil.getSpellHandler(target);
                    var activeSpells = targetHandler.getActiveSpells();
                    this.cleanse(target, 0, MobEffectCategory.HARMFUL);
                    targetHandler.getBuffs().stream().filter(SkillBuff::isBeneficial).forEach(skillBuff -> removeSkillBuff(target, skillBuff.skill()));
                    for (AbstractSpell spell : activeSpells) {
                        spell.endSpell();
                    }

                    if (skills.hasSkill(SBSkills.DOMINANT_MAGIC.value()))
                        addSkillBuff(
                                target,
                                SBSkills.DOMINANT_MAGIC,
                                BuffCategory.HARMFUL,
                                SkillBuff.MOB_EFFECT,
                                new MobEffectInstance(SBEffects.SILENCED, 100, 0, false, false),
                                100
                        );

                    if (skills.hasSkill(SBSkills.RESIDUAL_DISRUPTION.value())) {
                        addSkillBuff(
                                target,
                                SBSkills.RESIDUAL_DISRUPTION,
                                BuffCategory.HARMFUL,
                                SkillBuff.SPELL_MODIFIER,
                                SpellModifier.RESIDUAL_DISRUPTION,
                                100
                        );
                    }

                    if (skills.hasSkill(SBSkills.UNFOCUSED))
                        addSkillBuff(
                                target,
                                SBSkills.UNFOCUSED,
                                BuffCategory.HARMFUL,
                                SkillBuff.SPELL_MODIFIER,
                                SpellModifier.UNFOCUSED,
                                200
                        );

                    if (skills.hasSkill(SBSkills.MAGIC_POISONING.value()))
                        targetHandler.consumeMana(Math.max(20 * activeSpells.size(), 20));

                    if (skills.hasSkill(SBSkills.NULLIFICATION.value())) {
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
                    if (skills.hasSkillReady(SBSkills.EXPUNGE.value()) && context.hasCatalyst(SBItems.FOOL_SHARD.get()) && !spellList.isEmpty()) {
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
        return context.isChoice(SBSkills.COUNTER_MAGIC) ? 200 : super.getDuration(context);
    }

    @Override
    protected void registerSkillTooltips() {

    }
}
