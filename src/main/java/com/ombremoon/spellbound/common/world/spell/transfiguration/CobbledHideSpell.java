package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class CobbledHideSpell extends AnimatedSpell {
    private static final ResourceLocation hideMod = CommonClass.customLocation("hide_spell_mod");
    private static final UUID PRE_DAMAGE = UUID.fromString("07e4170f-6d15-4ae6-9531-b727cfe39f96");

    private List<LivingEntity> buffedTargets;

    private static Builder<CobbledHideSpell> hideSpellBuilder() {
        return createSimpleSpellBuilder(CobbledHideSpell.class)
                .manaCost(30)
                .castTime(20)
                .duration(1200);
    }

    public CobbledHideSpell(SpellType<?> spellType) {
        super(spellType, hideSpellBuilder());
    }


    @Override
    protected void registerSkillTooltips() {

    }

    //Damage based skills needed to be added
    @Override
    protected void onSpellStart(SpellContext context) {
        if (context.isRecast()) return;
        LivingEntity caster = context.getCaster();
        SkillHolder skills = context.getSkills();
        Holder<SoundEvent> sound = SoundEvents.ARMOR_EQUIP_LEATHER;

        float modAmount = 1.1f;
        if (skills.hasSkill(SBSkills.DRAGON_HIDE)){
            modAmount = 1.5f;
            sound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        } else if (skills.hasSkill(SBSkills.DIAMOND_HIDE)) {
            modAmount = 1.3f;
            sound = SoundEvents.ARMOR_EQUIP_DIAMOND;
        } else if (skills.hasSkill(SBSkills.IRON_HIDE)) {
            modAmount = 1.2f;
            sound = SoundEvents.ARMOR_EQUIP_IRON;
        }

        addSkillBuff(
                context.getCaster(),
                SBSkills.COBBLED_HIDE,
                BuffCategory.BENEFICIAL,
                SkillBuff.ATTRIBUTE_MODIFIER,
                new ModifierData(Attributes.ARMOR, new AttributeModifier(hideMod, modAmount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                getDuration());

        if (skills.hasSkill(SBSkills.INFECTIOUS)) {
            Predicate<LivingEntity> predicate;
            if (skills.hasSkill(SBSkills.VIRAL))
                predicate = entity -> entity instanceof Player || (entity instanceof OwnableEntity ownable && context.getCaster() == ownable.getOwner());
            else predicate = entity -> entity instanceof OwnableEntity ownable && context.getCaster() == ownable.getOwner();

            this.buffedTargets = context.getLevel().getEntitiesOfClass(LivingEntity.class,
                    context.getCaster().getBoundingBox().inflate(8d), predicate);

            double allyBuff = skills.hasSkill(SBSkills.REINFORCED) ? modAmount : modAmount/2d;
            buffedTargets.forEach(mob -> mob.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(
                    hideMod, allyBuff, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            )));
        }

        context.getLevel()
                .playSeededSound(
                        null, caster.getX(), caster.getY(), caster.getZ(), sound, caster.getSoundSource(), 1.0F, 1.0F, caster.getRandom().nextLong()
                );
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return false;
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        if (buffedTargets != null) {
            buffedTargets.forEach(mob -> mob.getAttribute(Attributes.ARMOR).removeModifier(hideMod));
        }

        context.getCaster().getAttribute(Attributes.ARMOR).removeModifier(hideMod);
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        if (context.getSkills().hasSkill(SBSkills.REPULSIVE_SKIN)) {
            LivingEntity caster = context.getCaster();
            List<LivingEntity> entities = context.getLevel().getEntitiesOfClass(LivingEntity.class,
                    caster.getBoundingBox().inflate(8d));

            for (LivingEntity entity : entities) {
                entity.hurt(context.getCaster().damageSources().magic(), 2f);
                entity.knockback(1d,
                        entity.getX() - caster.getX(),
                        entity.getZ() - caster.getZ());
            }

            endSpell();
        }
    }
}
