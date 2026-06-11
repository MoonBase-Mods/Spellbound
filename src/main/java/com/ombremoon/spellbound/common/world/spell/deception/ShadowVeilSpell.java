/*
package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.spell.ShadowVeil;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
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
    private static final List<SoundEvent> MOB_SOUNDS = List.of(
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.SKELETON_AMBIENT,
            SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.SPIDER_AMBIENT
    );
    private static final SpellDataKey<Integer> VEIL_ID = SyncedSpellData.registerDataKey(ShadowVeilSpell.class, SBDataTypes.INT.get());

    private Map<LivingEntity, Integer> VEIL_ATTENDEES = new HashMap<>();
    private List<LivingEntity> BEEN_FEARED = new ArrayList<>();
    private int soundRate = 0;

    private static Builder<ShadowVeilSpell> createShadowVeilSpell() {
        return createSimpleSpellBuilder(ShadowVeilSpell.class)
                .castCondition((context, shadowVeilSpell) -> shadowVeilSpell.hasValidSpawnPos())
                .manaCost(15)
                .duration(200);
    }

    public ShadowVeilSpell() {
        super(SBSpells.SHADOW_VEIL.get(), createShadowVeilSpell());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            ShadowVeil veil = this.summonEntity(context, SBEntities.SHADOW_VEIL.get(), shadowVeil -> {
                shadowVeil.setCaster(caster);
            });

            setVeil(veil);
        } else this.soundRate = level.getRandom().nextInt(1, 4) * 20;


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
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
            if (skills.hasSkill(SBSkills.SHADOW_DOMAIN)) entity.setData(SBData.SHADOW_DOMAIN_VEIL, veil.getId());
            if (level.isClientSide() && skills.hasSkill(SBSkills.DECEPTIVE_ECHOES) && this.tickCount % this.soundRate == 0) playRandomMobSound(level, entity);

            int timeInVeil = VEIL_ATTENDEES.getOrDefault(entity, 0);
            timeInVeil++;
            if (timeInVeil >= 5 && !BEEN_FEARED.contains(entity) && skills.hasSkill(SBSkills.SAPPING_FEAR)) {
                SpellUtil.getSpellHandler(caster).applyFearEffect(entity, 40);
                BEEN_FEARED.add(entity);
            }
            VEIL_ATTENDEES.put(entity, timeInVeil);
        } else if (SpellUtil.IS_ALLIED.test(caster, entity)) {
            if (skills.hasSkill(SBSkills.DEEP_NIGHT) || (skills.hasSkill(SBSkills.IN_THE_SHADOWS) && tickCount % 60 == 0))
                entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40));
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
            if (veil.getId() == veilId) entity.setData(SBData.SHADOW_DOMAIN_VEIL, 0);
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
}
*/
