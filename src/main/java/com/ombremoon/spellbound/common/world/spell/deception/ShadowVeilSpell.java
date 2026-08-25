package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.DealtDamageEvent;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.entity.spell.ShadowVeil;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShadowVeilSpell extends AnimatedSpell {
    private static final ResourceLocation INVISIBILITY_EFFECT = CommonClass.customLocation("shadow_veil_invisibility");
    private static final ResourceLocation SHADOW_VEIL = CommonClass.customLocation("shadow_veil");
    private static final ResourceLocation CLOUDED_SENSES = CommonClass.customLocation("clouded_senses");
    private static final ResourceLocation HIDDEN_WOUNDS = CommonClass.customLocation("hidden_wounds");
    private static final List<SoundEvent> MOB_SOUNDS = List.of(
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.SKELETON_AMBIENT,
            SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.SPIDER_AMBIENT
    );
    private static final SpellDataKey<Integer> VEIL_ID = SyncedSpellData.registerDataKey(ShadowVeilSpell.class, SBDataTypes.INT.get());

    private final Map<LivingEntity, Integer> VEIL_ATTENDEES = new HashMap<>();
    private final List<LivingEntity> BEEN_FEARED = new ArrayList<>();
    private Vec3 mistPos = null;
    private int soundRate = 0;

    private static Builder<ShadowVeilSpell> createShadowVeilSpell() {
        return createSimpleSpellBuilder(ShadowVeilSpell.class)
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

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(VEIL_ID, 0);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            if (this.mistPos == null) {
                this.mistPos = this.getSpawnVec();
                if (this.mistPos == null)
                    this.mistPos = caster.position().relative(Direction.DOWN, 0.5);
            }

            this.summonEntity(context, SBEntities.SHADOW_VEIL.get(), this.mistPos, this::setVeil);
        } else {
            this.soundRate = level.getRandom().nextInt(1, 4) * 20;
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        ShadowVeil veil = getVeil(context.getLevel());
        if (veil == null)
            return;

        for (LivingEntity entity : VEIL_ATTENDEES.keySet()) {
            int veilId = entity.getData(SBData.SHADOW_DOMAIN_VEIL);
            if (veil.getId() == veilId) {
                entity.setData(SBData.SHADOW_DOMAIN_VEIL, 0);
            }
        }

        veil.discard();
    }

    public void addVeilEffects(SpellContext context, LivingEntity entity, ShadowVeil veil) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        var handler = context.getSpellHandler();
        if (SpellUtil.CAN_ATTACK_ENTITY.test(caster, entity)) {
            this.addSkillBuff(
                    entity,
                    SBSkills.SHADOW_VEIL,
                    SHADOW_VEIL,
                    BuffCategory.HARMFUL,
                    SkillBuff.MOB_EFFECT,
                    new MobEffectInstance(MobEffects.BLINDNESS, -1)
            );

            if (context.hasSkill(SBSkills.CLOUDED_SENSES)) {
                this.addEventBuff(
                        entity,
                        SBSkills.CLOUDED_SENSES,
                        BuffCategory.HARMFUL,
                        SpellEventListener.Events.DEALT_DAMAGE_PRE,
                        CLOUDED_SENSES,
                        pre -> {
                            LivingEntity attacker = pre.getAttacker();
                            if (attacker.level().getRandom().nextInt(4) == 0) {
                                pre.setNewDamage(0);
                            }
                        }
                );
            }

            if (context.hasSkill(SBSkills.SHADOW_DOMAIN)) {
                entity.setData(SBData.SHADOW_DOMAIN_VEIL, veil.getId());
            }

            if (context.hasSkill(SBSkills.HIDDEN_WOUNDS)) {
                this.addSkillBuff(
                        entity,
                        SBSkills.HIDDEN_WOUNDS,
                        HIDDEN_WOUNDS,
                        BuffCategory.HARMFUL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(SBEffects.OBFUSCATED, -1)
                );
            }
        } else if (SpellUtil.IS_ALLIED.test(caster, entity)) {
            if (context.hasSkill(SBSkills.DEEP_NIGHT)) {
                addSkillBuff(
                        entity,
                        SBSkills.DEEP_NIGHT,
                        INVISIBILITY_EFFECT,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, -1)
                );
            } else if (context.hasSkill(SBSkills.IN_THE_SHADOWS) && tickCount % 60 == 0) {
                addSkillBuff(
                        entity,
                        SBSkills.IN_THE_SHADOWS,
                        INVISIBILITY_EFFECT,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, 40)
                );
            }
        }
    }

    public void tickVeilEffects(SpellContext context, LivingEntity entity, ShadowVeil veil) {

    }

    public void removeVeilEffects(SpellContext context, LivingEntity entity) {
        LivingEntity caster = context.getCaster();
        if (SpellUtil.CAN_ATTACK_ENTITY.test(caster, entity)) {
            this.removeSkillBuff(entity, SBSkills.SHADOW_VEIL);
            this.removeSkillBuff(entity, SBSkills.CLOUDED_SENSES);
            this.removeSkillBuff(entity, SBSkills.HIDDEN_WOUNDS);
        } else if (SpellUtil.IS_ALLIED.test(caster, entity)) {
            this.removeSkillBuff(entity, SBSkills.IN_THE_SHADOWS);
            this.removeSkillBuff(entity, SBSkills.DEEP_NIGHT);
        }
    }

    private void playRandomMobSound(Level level, LivingEntity target) {
        if (!(target instanceof Player player)) return;
        BlockPos pos = target.getOnPos().relative(target.getDirection().getOpposite());
        level.playSound(player, pos, MOB_SOUNDS.get(level.getRandom().nextInt(MOB_SOUNDS.size())), SoundSource.HOSTILE);
    }

    public void setVeil(ShadowVeil veil) {
        this.spellData.set(VEIL_ID, veil.getId());
    }

    public ShadowVeil getVeil(Level level) {
        if (this.spellData.get(VEIL_ID) == null) return null;
        return (ShadowVeil) level.getEntity(this.spellData.get(VEIL_ID));
    }

    public void setMistPos(Vec3 pos) {
        this.mistPos = pos;
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
