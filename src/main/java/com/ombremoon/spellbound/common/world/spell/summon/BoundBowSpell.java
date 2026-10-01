package com.ombremoon.spellbound.common.world.spell.summon;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;
import com.ombremoon.spellbound.common.world.entity.projectile.BoundArrow;
import com.ombremoon.spellbound.common.world.spell.transfiguration.CreateObjectSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class BoundBowSpell extends SummonSpell implements RadialSpell {
    public static final ResourceLocation FIRE_ARROW = CommonClass.customLocation("fire_arrow");
    public static final ResourceLocation ICE_ARROW = CommonClass.customLocation("ice_arrow");
    public static final ResourceLocation SHOCK_ARROW = CommonClass.customLocation("shock_arrow");
    public static Builder<BoundBowSpell> createBoundBowSpellBuilder() {
        return createSummonBuilder(BoundBowSpell.class)
                .castCondition((context, boundBowSpell) -> {
                    if (boundBowSpell.disableChoiceOnRecast(context, boundBowSpell)) {
                        return false;
                    } else if (!context.isChoice(SBSkills.BOUND_MARKSMAN)) {
                        boundBowSpell.assignVariant(context);
                        LivingEntity caster = context.getCaster();
                        return caster.getMainHandItem().isEmpty();
                    }

                    return boundBowSpell.hasValidSpawnPos();
                })
                .duration(1200);
    }
    private int imbuedSlot;
    private int shotCount;
    private BoundArrow.ArrowVariant variant;
    private ResourceLocation prevChoice;

    public BoundBowSpell() {
        super(SBSpells.BOUND_BOW.get(), createBoundBowSpellBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        ItemStack boundBow = SBItems.BOUND_BOW.get().getDefaultInstance();
        if (!level.isClientSide) {
            Imbuement imbuement = Imbuement.create(this);
            if (context.isChoice(SBSkills.BOUND_MARKSMAN)) {

            } else if (this.giveSpellItem(boundBow, imbuement) && caster instanceof Player player) {
                this.imbuedSlot = player.getInventory().selected;
            }

            this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("bound_bow_cast"),
                    caster.getId(), EntityEffectExecutor.AutoRotate.NONE).setOffset(0, -0.3, 0));
            level.playSound(null, context.getCaster().blockPosition(), SoundEvents.CROSSBOW_LOADING_START.value(),
                    SoundSource.PLAYERS,0.4F + level.random.nextFloat() * 0.2F ,0.8F + level.random.nextFloat() * 0.2F);
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide && this.isInfusedChoice(context) && !context.isChoice(this.prevChoice)) {
            this.assignVariant(context);
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        LivingEntity caster = context.getCaster();
        if (!this.isChoice(SBSkills.BOUND_MARKSMAN))
            this.removeSpellItem(caster, this.imbuedSlot);
    }

    @Override
    public boolean disableChoiceOnRecast(SpellContext context, AbstractSpell spell) {
        var handler = context.getSpellHandler();
        if (context.isRecast()) {
           BoundBowSpell boundBowSpell = handler.getSpell(SBSpells.BOUND_BOW.get());
           if (boundBowSpell.isInfusedChoice() && this.isInfusedChoice(context) || boundBowSpell.isChoice(SBSkills.BOUND_MARKSMAN) && context.isChoice(SBSkills.BOUND_MARKSMAN)) {
               boundBowSpell.endSpell();
               return true;
           }
        }

        return false;
    }

    public int getBowManaCost(SpellContext context) {
        return context.hasSkill(SBSkills.MYSTIC_MARKSMAN) ? 0 : 7;
    }

    public Imbuement createInfusedShot() {
        return Imbuement.create(this, DamageInstance.of(this.variant.getDamageType(), potencyWithLevel(2F)), -1);
    }

    public void assignVariant(SpellContext context) {
        if (isFireArrow(context)) {
            this.variant = BoundArrow.ArrowVariant.FIRE;
            this.prevChoice = FIRE_ARROW;
        } else if (isIceArrow(context)) {
            this.variant = BoundArrow.ArrowVariant.ICE;
            this.prevChoice = ICE_ARROW;
        } else if (isShockArrow(context)) {
            this.variant = BoundArrow.ArrowVariant.SHOCK;
            this.prevChoice = SHOCK_ARROW;
        } else {
            this.variant = BoundArrow.ArrowVariant.MAGIC;
            this.prevChoice = this.location();
        }
    }

    public BoundArrow.ArrowVariant getInfusedShot() {
        return this.variant;
    }

    private boolean isInfusedChoice(SpellContext context) {
        return context.isChoice(SBSkills.BOUND_BOW) || isFireArrow(context) || isIceArrow(context) || isShockArrow(context);
    }

    private boolean isInfusedChoice() {
        return this.isChoice(SBSkills.BOUND_BOW) || this.isChoice(FIRE_ARROW) || this.isChoice(ICE_ARROW) || this.isChoice(SHOCK_ARROW);
    }

    public boolean isFireArrow(SpellContext context) {
        return context.isChoice(FIRE_ARROW);
    }

    public boolean isIceArrow(SpellContext context) {
        return context.isChoice(ICE_ARROW);
    }

    public boolean isShockArrow(SpellContext context) {
        return context.isChoice(SHOCK_ARROW);
    }

    public int getShotCount() {
        return this.shotCount;
    }

    public void incrementShotsFired() {
        this.shotCount++;
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
