package com.ombremoon.spellbound.common.world.spell.divine;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.DamageEvent;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.ConfigHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.UnknownNullability;

public class HealingTouchSpell extends AnimatedSpell {
    private static final ResourceLocation ARMOR_MOD = CommonClass.customLocation("oak_blessing_mod");
    private static final ResourceLocation TRANQUILITY = CommonClass.customLocation("tranquility");
    private static final ResourceLocation OVERGROWTH = CommonClass.customLocation("overgrowth");
    private static final ResourceLocation BLASPHEMY = CommonClass.customLocation("blasphemy");
    private static final ResourceLocation CONVALESCENCE = CommonClass.customLocation("convalescence");

    private int overgrowthStacks = 0;
    private int blessingDuration = 0;

    private static Builder<HealingTouchSpell> createHealingSpell() {
        return createSimpleSpellBuilder(HealingTouchSpell.class)
                .manaCost(15)
                .duration(100)
                .selfBuffCast()
                .fullRecast(true);
    }

    public HealingTouchSpell() {
        super(SBSpells.HEALING_TOUCH.get(), createHealingSpell());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.HEALING_TOUCH,
                SkillTooltip.HEAL.tooltip(this.getModifiedHeal(1)),
                SkillTooltip.DURATION.tooltip(100),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost())
        );
        this.addSkillDetails(SBSkills.BLASPHEMY,
                SkillTooltip.DISEASE_BUILD_UP.tooltip(potency(7))
        );
        this.addSkillDetails(SBSkills.DIVINE_BALANCE,
                SkillTooltip.MODIFY_DURATION.tooltip(100F),
                SkillTooltip.MODIFY_MANA_COST.tooltip(50F)
        );
        this.addSkillDetails(SBSkills.NATURES_TOUCH,
                SkillTooltip.HEAL.tooltip(this.getModifiedHeal(4))
        );
        this.addSkillDetails(SBSkills.HEALING_STREAM,
                SkillTooltip.MANA_TO_HEALTH.tooltip(potency(2F)),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.TRANQUILITY_OF_WATER,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(SBAttributes.MANA_REGEN, new AttributeModifier(TRANQUILITY, potency(0.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.ACCELERATED_GROWTH,
                SkillTooltip.HUNGER.tooltip((int) potency(2F))
        );
        this.addSkillDetails(SBSkills.OVERGROWTH,
                SkillTooltip.ATTRIBUTE_PER_CHARGE.tooltip(new ModifierData(Attributes.MAX_HEALTH, new AttributeModifier(OVERGROWTH, potency(0.1F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.MAX_CHARGES.tooltip(5),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.OAK_BLESSING,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ARMOR, new AttributeModifier(ARMOR_MOD, potency(0.15F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.HP_THRESHOLD.tooltip(30F),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.COOLDOWN.tooltip(6000),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        if (!context.getLevel().isClientSide) {
            LivingEntity caster = context.getCaster();
            if (context.hasSkill(SBSkills.NATURES_TOUCH))
                this.heal(caster, 2F);

            if (context.hasSkill(SBSkills.CLEANSING_TOUCH))
                this.cleanseCaster(1);

            if (context.hasSkill(SBSkills.ACCELERATED_GROWTH) && caster instanceof Player player)
                player.getFoodData().eat((int) potency(1), 1.0F);

            if (context.hasSkill(SBSkills.TRANQUILITY_OF_WATER))
                this.addSkillBuff(
                        caster,
                        SBSkills.TRANQUILITY_OF_WATER,
                        TRANQUILITY,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(SBAttributes.MANA_REGEN, new AttributeModifier(TRANQUILITY, potency(0.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                );

            if (context.hasSkill(SBSkills.CONVALESCENCE)) {
                this.addEventBuff(
                        caster,
                        SBSkills.CONVALESCENCE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.EFFECT_APPLICABLE,
                        CONVALESCENCE,
                        event -> {
                            if (event.getEffectInstance().getEffect() == SBEffects.DISEASE)
                                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                        }
                );
            }

            if (context.hasSkillReady(SBSkills.BLASPHEMY)) {
                this.addEventBuff(
                        caster,
                        SBSkills.CONVALESCENCE,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.POST_DAMAGE,
                        CONVALESCENCE,
                        post -> {
                            if (post.getSource().getEntity() instanceof LivingEntity attacker) {
                                this.incrementEffect(attacker, EffectManager.Effect.DISEASE, potency(7));
                            }
                        }
                );
            }
            //VFX
            this.triggerSpellFX(EffectData.Entity.of( CommonClass.customLocation("healing_touch_cast"),
                    caster.getId(), EntityEffectExecutor.AutoRotate.NONE).setOffset(0, -0.8,0));

            playCastSound(context.getLevel(), context);
        }

    }

    public void playCastSound(Level level, SpellContext context) {
        float volume = 0.2F + level.random.nextFloat() * 0.2F;
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;
        level.playSound(null, context.getCaster().blockPosition(), SpellboundSounds.MAGIC_SPARKLES.get(),
                SoundSource.PLAYERS, volume*0.15F, 0.2F*pitch);
        level.playSound(null, context.getCaster().blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS, volume, pitch);

    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        var handler = context.getSpellHandler();
        if (!context.getLevel().isClientSide) {
            double maxMana = caster.getAttribute(SBAttributes.MAX_MANA).getValue();

            float heal = 1;
            if (context.hasSkill(SBSkills.HEALING_STREAM))
                heal += (float) (maxMana - handler.getMana()) * potency(0.02F);

            this.heal(caster, heal);


            if (context.hasSkill(SBSkills.OVERGROWTH) && this.overgrowthStacks <= 5 && caster.getHealth() >= caster.getMaxHealth()) {
                this.addSkillBuff(
                        caster,
                        SBSkills.OVERGROWTH,
                        OVERGROWTH,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.MAX_HEALTH, new AttributeModifier(OVERGROWTH, potency(0.1F) * this.overgrowthStacks, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                        200
                );
                this.overgrowthStacks++;
            }

            if (context.hasSkillReady(SBSkills.OAK_BLESSING) && caster.getHealth() < caster.getMaxHealth() * 0.3) {
                this.addSkillBuff(
                        caster,
                        SBSkills.OAK_BLESSING,
                        ARMOR_MOD,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.ARMOR, new AttributeModifier(ARMOR_MOD, potency(0.15F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                        200
                );
                this.addCooldown(SBSkills.OAK_BLESSING, 6000);
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        this.removeSkillBuff(caster, SBSkills.TRANQUILITY_OF_WATER);
        this.removeSpellFX(CommonClass.customLocation("healing_touch_cast"));
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        int level = 5 - context.getSpellLevel();
        int i = 20 + (level * 5);
        return this.tickCount % i == 0;
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
