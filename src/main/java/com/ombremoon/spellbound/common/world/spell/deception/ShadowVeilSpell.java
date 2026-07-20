package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.client.event.SpellCastEvents;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.DealtDamageEvent;
import com.ombremoon.spellbound.common.magic.api.events.PlayerAttackEvent;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.entity.spell.ShadowVeil;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShadowVeilSpell extends AnimatedSpell {
    private static final ResourceLocation INVISIBILITY_EFFECT = CommonClass.customLocation("shadow_veil_invisibility");
    private static final ResourceLocation BLINDNESS_EFFECT = CommonClass.customLocation("shadow_veil_blindness");
    private static final ResourceLocation DAMAGE_EVENT = CommonClass.customLocation("shadow_veil_attack_miss");
    private static final ResourceLocation HIDDEN_WOUNDS_EFFECT = CommonClass.customLocation("shadow_veil_obfuscated");
    private static final List<SoundEvent> MOB_SOUNDS = List.of(
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.SKELETON_AMBIENT,
            SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.SPIDER_AMBIENT
    );
    private static final SpellDataKey<Integer> VEIL_ID = SyncedSpellData.registerDataKey(ShadowVeilSpell.class, SBDataTypes.INT.get());

    private final Map<LivingEntity, Integer> VEIL_ATTENDEES = new HashMap<>();
    private final List<LivingEntity> BEEN_FEARED = new ArrayList<>();
    private int soundRate = 0;

    private static Builder<ShadowVeilSpell> createShadowVeilSpell() {
        return createSimpleSpellBuilder(ShadowVeilSpell.class)
                .castCondition((context, shadowVeilSpell) -> shadowVeilSpell.hasValidSpawnPos())
                .manaCost(15)
                .duration(200)
                .fullRecast(true);
    }

    public ShadowVeilSpell() {
        super(SBSpells.SHADOW_VEIL.get(), createShadowVeilSpell());
    }

    @Override
    public void registerSkillTooltips() {

    }

    //What to do when spell starts
    @Override
    protected void onSpellStart(SpellContext context) {
        //Gets the caster from the context
        LivingEntity caster = context.getCaster();
        //Gets the level from the context
        Level level = context.getLevel();
        //Checks if it is on the server or client
        if (!level.isClientSide()) {
            //Summons the entity
            ShadowVeil veil = this.summonEntity(context, SBEntities.SHADOW_VEIL.get(), shadowVeil -> {
                //Sets the caster on the entity
                shadowVeil.setCaster(caster);
            });

            //Saves the veil to spell data
            setVeil(veil);
        } else {
            //Picks a random rate for how often the sounds will play (if skill unlocked)
            this.soundRate = level.getRandom().nextInt(1, 4) * 20;
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        ShadowVeil veil = getVeil(level);
        if (veil == null) return;

        List<Entity> list = level.getEntitiesOfClass(Entity.class, veil.getBoundingBox());
        for (Entity entity : list) {
            if (entity instanceof LivingEntity livingEntity
                    && entity.isAlive()
                    && !entity.isSpectator()) {
                livingEntityInVeil(context.getSkills(), caster, level, livingEntity, veil);
            }
            if (entity instanceof Projectile projectile) {
                projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.25D));
            }
        }
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return tickCount % 20 == 0;
    }

    private void livingEntityInVeil(SkillHolder skills, LivingEntity caster, Level level, LivingEntity entity, ShadowVeil veil) {
        if (SpellUtil.CAN_ATTACK_ENTITY.test(caster, entity)) {
            addSkillBuff(
                    entity,
                    SBSkills.SHADOW_VEIL,
                    BLINDNESS_EFFECT,
                    BuffCategory.HARMFUL,
                    SkillBuff.MOB_EFFECT,
                    new MobEffectInstance(MobEffects.BLINDNESS, 40)
            );

            if (skills.hasSkill(SBSkills.CLOUDED_SENSES)) {
                addEventBuff(
                        entity,
                        SBSkills.CLOUDED_SENSES,
                        BuffCategory.HARMFUL,
                        SpellEventListener.Events.DEALT_DAMAGE_PRE,
                        DAMAGE_EVENT,
                        this::onEntityAttack
                );
            }

            if (skills.hasSkill(SBSkills.SHADOW_DOMAIN)) {
                entity.setData(SBData.SHADOW_DOMAIN_VEIL, veil.getId());
            }
            if (skills.hasSkill(SBSkills.HIDDEN_WOUNDS)) {
                addSkillBuff(
                        entity,
                        SBSkills.HIDDEN_WOUNDS,
                        HIDDEN_WOUNDS_EFFECT,
                        BuffCategory.HARMFUL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(SBEffects.OBFUSCATED, 40)
                );
            }
            if (level.isClientSide() && skills.hasSkill(SBSkills.DECEPTIVE_ECHOES) && this.tickCount % this.soundRate == 0) {
                playRandomMobSound(level, entity);
            }

            int timeInVeil = VEIL_ATTENDEES.getOrDefault(entity, 0);
            timeInVeil++;
            if (timeInVeil >= 5 && !BEEN_FEARED.contains(entity) && skills.hasSkill(SBSkills.SAPPING_FEAR)) {
                SpellUtil.getSpellHandler(caster).applyFearEffect(entity, veil.position(), 40);
                BEEN_FEARED.add(entity);
            }
            VEIL_ATTENDEES.put(entity, timeInVeil);

        } else if (SpellUtil.IS_ALLIED.test(caster, entity)) {
            if (skills.hasSkill(SBSkills.DEEP_NIGHT)) {
                addSkillBuff(
                        entity,
                        SBSkills.DEEP_NIGHT,
                        INVISIBILITY_EFFECT,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(MobEffects.INVISIBILITY, 40)
                );
            } else if (skills.hasSkill(SBSkills.IN_THE_SHADOWS) && tickCount % 60 == 0) {
                addSkillBuff(
                        entity,
                        SBSkills.IN_THE_SHADOWS,
                        INVISIBILITY_EFFECT,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(MobEffects.INVISIBILITY, 40)
                );
            }
        }
    }

    private void onEntityAttack(DealtDamageEvent.Pre pre) {
        //Caster is the target of the skill in this instance (the one that has a chance to miss)
        LivingEntity caster = pre.getCaster();
        ShadowVeil veil = getVeil(caster.level());
        if (veil.getBoundingBox().intersects(caster.getBoundingBox()) && SpellUtil.CAN_ATTACK_ENTITY.test(caster, pre.getTarget())) {
            if (caster.level().getRandom().nextInt(4) == 0) {
                pre.setNewDamage(0);
            }
        }
    }

    private void playRandomMobSound(Level level, LivingEntity target) {
        if (!(target instanceof Player player)) return;
        BlockPos pos = target.getOnPos().relative(target.getDirection().getOpposite());
        level.playSound(player, pos, MOB_SOUNDS.get(level.getRandom().nextInt(MOB_SOUNDS.size())), SoundSource.HOSTILE);
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        ShadowVeil veil = getVeil(context.getLevel());
        if (veil == null) return;
        for (LivingEntity entity : VEIL_ATTENDEES.keySet()) {
            int veilId = entity.getData(SBData.SHADOW_DOMAIN_VEIL);
            if (veil.getId() == veilId) {
                entity.setData(SBData.SHADOW_DOMAIN_VEIL, 0);
            }
            removeSkillBuff(entity, SBSkills.CLOUDED_SENSES);
        }
        veil.discard();
    }

    public void setVeil(ShadowVeil veil) {
        this.spellData.set(VEIL_ID, veil.getId());
    }

    public ShadowVeil getVeil(Level level) {
        if (this.spellData.get(VEIL_ID) == null) return null;
        return (ShadowVeil) level.getEntity(this.spellData.get(VEIL_ID));
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(VEIL_ID, 0);
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
