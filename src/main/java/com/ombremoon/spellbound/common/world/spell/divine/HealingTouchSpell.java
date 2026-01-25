package com.ombremoon.spellbound.common.world.spell.divine;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBParticles;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.DamageEvent;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.UnknownNullability;

public class HealingTouchSpell extends AnimatedSpell {
    private static final ResourceLocation ARMOR_MOD = CommonClass.customLocation("oak_blessing_mod");
    private static final ResourceLocation TRANQUILITY = CommonClass.customLocation("tranquility");
    private static final ResourceLocation OVERGROWTH = CommonClass.customLocation("overgrowth");
    private static final ResourceLocation PLAYER_DAMAGE = CommonClass.customLocation("healing_touch_player_damage");

    private int overgrowthStacks = 0;
    private int blessingDuration = 0;

    private static Builder<HealingTouchSpell> createHealingSpell() {
        return createSimpleSpellBuilder(HealingTouchSpell.class)
                .manaCost(15)
                .duration(100)
                .selfBuffCast()
                .fullRecast();
    }


    //TODO: Hunger/hp/mana numbers need refining
    public HealingTouchSpell() {
        super(SBSpells.HEALING_TOUCH.get(), createHealingSpell());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        context.getSpellHandler().getListener().addListener(SpellEventListener.Events.POST_DAMAGE, PLAYER_DAMAGE, this::onDamagePost);
        if (!context.getLevel().isClientSide) {
            LivingEntity caster = context.getCaster();
            if (context.hasSkill(SBSkills.NATURES_TOUCH))
                this.heal(caster, 4f);

            if (context.hasSkill(SBSkills.CLEANSING_TOUCH))
                this.cleanseCaster(1);

            if (context.hasSkill(SBSkills.TRANQUILITY_OF_WATER))
                this.addSkillBuff(
                        caster,
                        SBSkills.TRANQUILITY_OF_WATER,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(SBAttributes.MANA_REGEN, new AttributeModifier(TRANQUILITY, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                );
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        var handler = context.getSpellHandler();
        if (!context.getLevel().isClientSide) {
            double maxMana = caster.getAttribute(SBAttributes.MAX_MANA).getValue();

            float heal = 2;
            if (context.hasSkill(SBSkills.HEALING_STREAM))
                heal += (float) (maxMana - handler.getMana()) * 0.02f;

            this.heal(caster, heal);

            if (context.hasSkill(SBSkills.ACCELERATED_GROWTH) && caster instanceof Player player) {
                player.getFoodData().eat(1, 1.0F);
            }

            if (context.hasSkill(SBSkills.OVERGROWTH) && this.overgrowthStacks <= 5 && caster.getHealth() >= caster.getMaxHealth()) {
                this.removeSkillBuff(caster, SBSkills.OVERGROWTH);
                this.addSkillBuff(
                        caster,
                        SBSkills.OVERGROWTH,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.MAX_HEALTH, new AttributeModifier(OVERGROWTH, 0.1 * this.overgrowthStacks, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                        200
                );
                this.overgrowthStacks++;
            }

            if (context.hasSkill(SBSkills.OAK_BLESSING) && caster.getHealth() < caster.getMaxHealth() * 0.3) {
                this.addSkillBuff(
                        caster,
                        SBSkills.OAK_BLESSING,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.ARMOR, new AttributeModifier(ARMOR_MOD, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                        200
                );
            }
        }

        for (int j = 0; j < 5; j++) {
            this.createSurroundingParticles(caster, SBParticles.GOLD_HEART.get(), 1);
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        this.removeSkillBuff(caster, SBSkills.TRANQUILITY_OF_WATER);
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        int level = 5 - context.getSpellLevel();
        int i = 20 + (level * 5);
        return this.tickCount % i == 0;
    }

    private void onDamagePost(DamageEvent.Post event) {
   /*     SkillHolder skills = SpellUtil.getSkills(caster);

        if (event.getEntity().hasEffect(MobEffects.POISON) && skills.hasSkill(SBSkills.CONVALESCENCE.value()))
            caster.heal(1);

        if (skills.hasSkillReady(SBSkills.BLASPHEMY.value())) {
            EffectManager status = event.getEntity().getData(SBData.STATUS_EFFECTS);
//            status.increment(EffectManager.Effect.DISEASE, 100);
            addCooldown(SBSkills.BLASPHEMY, 100);
        }*/
    }

    @Override
    protected void registerSkillTooltips() {

    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag nbt = super.saveData(compoundTag);
        nbt.putInt("overgrowth", this.overgrowthStacks);
        nbt.putInt("blessing", this.blessingDuration);
        return nbt;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        this.overgrowthStacks = nbt.getInt("overgrowth");
        this.blessingDuration = nbt.getInt("blessing");
    }

}
