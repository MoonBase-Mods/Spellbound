package com.ombremoon.spellbound.common.world.familiars;

import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.common.magic.api.events.DeathEvent;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.world.entity.living.familiars.CatEntity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

public class CatFamiliar extends Familiar<CatEntity> {
    public static final ResourceLocation DEATH_EVENT = CommonClass.customLocation("nine_lives_event");
    public static final ResourceLocation BLEED_BUFF = CommonClass.customLocation("bleed_buff");

    public CatFamiliar(int bond, int rebirths) {
        super(bond, rebirths);
    }

    @Override
    public List<FamiliarAffinity> modifyFamiliarAttributes(LivingEntity familiar, FamiliarHandler handler, int rebirths, int bond) {
        return List.of(
                addAttributeModifier(familiar, SBAffinities.NATURAL_PREDATOR, Attributes.MAX_HEALTH, 1.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                addAttributeModifier(familiar, SBAffinities.NATURAL_PREDATOR, Attributes.ATTACK_DAMAGE, 1f, AttributeModifier.Operation.ADD_VALUE)
        );
    }

    @Override
    public void onSpawn(FamiliarHandler handler, BlockPos spawnPos) {
        super.onSpawn(handler, spawnPos);
        if (hasAffinity(handler, SBAffinities.NINE_LIVES))
            addFamiliarEventListener(handler,
                    SpellEventListener.Events.ENTITY_KILL,
                    DEATH_EVENT,
                    event -> deathEvent(handler, event));
    }

    @Override
    public void tick(FamiliarHandler handler, int tickCount) {
        super.tick(handler, tickCount);
        if (!hasAffinity(handler, SBAffinities.BLOOD_MAGIC)) return;

        var entities = handler.getLevel().getEntitiesOfClass(
                LivingEntity.class,
                handler.getActiveEntity().getBoundingBox().inflate(4d, 2d, 4d)
        );

        for (LivingEntity entity : entities) {
            if (entity.hasEffect(SBEffects.BLEED)) {
                addSkillBuff(
                        handler.getOwner(),
                        SBAffinities.BLOOD_MAGIC,
                        BLEED_BUFF,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.SPELL_MODIFIER,
                        SpellModifier.BLOOD_MAGIC,
                        SBAffinities.BLOOD_MAGIC.getCooldown());
                useAffinity(handler, SBAffinities.BLOOD_MAGIC);
                break;
            }
        }
    }

    @Override
    public boolean shouldTick(FamiliarHandler handler, int tickCount) {
        return tickCount % 3 == 0;
    }

    @Override
    public void onBondUp(FamiliarHandler handler, int oldLevel, int newLevel) {
        super.onBondUp(handler, oldLevel, newLevel);
        if (newLevel == SBAffinities.NINE_LIVES.getRequiredBond())
            addFamiliarEventListener(handler,
                    SpellEventListener.Events.ENTITY_KILL,
                    DEATH_EVENT,
                    event -> deathEvent(handler, event));
    }

    @Override
    public void onRebirth(FamiliarHandler handler, int rebirths) {
        super.onRebirth(handler, rebirths);
        removeFamiliarEventListener(handler, DEATH_EVENT);
    }

    @Override
    public void onRemove(FamiliarHandler handler, BlockPos removePos) {
        super.onRemove(handler, removePos);
        removeFamiliarEventListener(handler, DEATH_EVENT);
    }

    private void deathEvent(FamiliarHandler handler, DeathEvent deathEvent) {
        if (!hasAffinity(handler, SBAffinities.NINE_LIVES)) return;

        deathEvent.cancelEvent();
        LivingEntity fam = handler.getActiveEntity();

        fam.setHealth(fam.getMaxHealth()*0.2f);
        fam.level().playSound(fam, fam.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 1f, 1f);
        useAffinity(handler, SBAffinities.NINE_LIVES);
    }
}
