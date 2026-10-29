package com.ombremoon.spellbound.common.world.entity.living;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import com.ombremoon.spellbound.common.world.spell.summon.SummonWolfPackSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class Hellhound extends SummonedWolf {
    public Hellhound(EntityType<? extends Wolf> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createHellhoundAttributes() {
        return Wolf.createAttributes().add(SBAttributes.MAGIC_RESIST, 0.15).add(SBAttributes.FIRE_SPELL_RESIST, 0.4).add(SBAttributes.FROST_SPELL_RESIST, -0.25).add(SBAttributes.SHOCK_SPELL_RESIST);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        AbstractSpell spell = SpellUtil.getActiveSpell(this);
        SpellDamageSource source = SpellDamageSource.fromVanillaSource(spell, this.damageSources().mobAttack(this))
                .addDamageInstance(DamageInstance.ofType(SBDamageTypes.RUIN_FIRE));

        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, source, f);
        }

        if (spell != null) {
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.BLOOD_SCENT) && entity instanceof LivingEntity living && living.getHealth() <= living.getMaxHealth() * 0.5) {
                f *= 1.5F;
            }
        }

        Entity entity1 = this.level().getEntity(this.getPackLeaderId());
        if (entity1 instanceof Hellhound alpha && alpha.isAlive()) {
            f *= 1.5F;
        }

        if (this.isPackLeader()) {
            f *= 2.0F;
        }

        boolean flag = entity.hurt(source, f);
        if (flag) {
            float f1 = this.getKnockback(entity, source);
            if (f1 > 0.0F && entity instanceof LivingEntity livingentity) {
                livingentity.knockback(
                        f1 * 0.5F,
                        Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        (-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
                );
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffects(serverlevel1, entity, source);
            }

            this.setLastHurtMob(entity);
            this.playAttackSound();
        }

        return flag;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SummonWolfPackSpell spell = this.getOrCreateSpell();
        if (spell != null) {
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.THICK_FUR)) {
                Holder<Biome> holder = level.getBiome(this.blockPosition());
                if (holder.is(Tags.Biomes.IS_HOT)) {
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttributeValue(Attributes.MAX_HEALTH) * 1.5);
                    this.getAttribute(Attributes.ARMOR).setBaseValue(5);
                    this.getAttribute(SBAttributes.MAGIC_RESIST).setBaseValue(this.getAttributeBaseValue(SBAttributes.MAGIC_RESIST) * 1.5);
                    this.getAttribute(SBAttributes.FIRE_SPELL_RESIST).setBaseValue(this.getAttributeBaseValue(SBAttributes.MAGIC_RESIST) * 1.5);
                }
            }
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void die(DamageSource cause) {
        SummonWolfPackSpell spell = this.getOrCreateSpell();
        if (!this.level().isClientSide && spell != null) {
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.BRIMSTONE)) {
                var targets = spell.getAttackableEntities(this, 2);
                for (LivingEntity target : targets) {
                    spell.hurt(target, SBDamageTypes.RUIN_FIRE, 3);
                }

                spell.triggerSpellFX(EffectData.DeadEntity.of(CommonClass.customLocation("flame_inferno"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                        .setScale(2, 2, 2));

                this.level()
                        .playSound(
                                null,
                                this.blockPosition(),
                                SoundEvents.GENERIC_EXPLODE.value(),
                                SoundSource.HOSTILE,
                                4.0F,
                                (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            }
        }

        super.die(cause);
    }
}
