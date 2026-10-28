package com.ombremoon.spellbound.common.world.entity.living;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import com.ombremoon.spellbound.common.world.spell.summon.SummonWolfPackSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class ShadowWolf extends SummonedWolf {
    public ShadowWolf(EntityType<? extends Wolf> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createShadowWolfAttributes() {
        return Wolf.createAttributes().add(SBAttributes.MAGIC_RESIST, 0.40).add(SBAttributes.FIRE_SPELL_RESIST).add(SBAttributes.FROST_SPELL_RESIST, 0.15).add(SBAttributes.SHOCK_SPELL_RESIST, -0.25);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && source.getEntity() instanceof LivingEntity living && this.getTarget() == living && this.isAlive()) {
            this.teleportTowards(living);
        }

        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        AbstractSpell spell = SpellUtil.getActiveSpell(this);
        SpellDamageSource source = SpellDamageSource.fromVanillaSource(spell, this.damageSources().mobAttack(this));
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, source, f);
        }

        if (spell != null) {
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.BLOOD_SCENT) && entity instanceof LivingEntity living && living.getHealth() <= living.getMaxHealth() * 0.5) {
                f *= 1.5F;
            }

            if (entity instanceof LivingEntity living && context.hasSkill(SBSkills.ASSASSINATE) && SpellUtil.isDark(this.level(), this.blockPosition())) {
                spell.addSkillBuff(
                        living,
                        SBSkills.ASSASSINATE,
                        CommonClass.customLocation("assassinate"),
                        BuffCategory.HARMFUL,
                        SkillBuff.MOB_EFFECT,
                        new MobEffectInstance(SBEffects.SILENCED, 60),
                        60
                );
            }
        }

        Entity entity1 = this.level().getEntity(this.getPackLeaderId());
        if (entity1 instanceof ShadowWolf alpha && alpha.isAlive()) {
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

            this.teleportTowards(entity);
        }

        return flag;
    }

    boolean teleportTowards(Entity target) {
        Vec3 vec3 = new Vec3(this.getX() - target.getX(), this.getY(0.5) - target.getEyeY(), this.getZ() - target.getZ());
        vec3 = vec3.normalize();
        double d1 = this.getX() + (this.random.nextDouble() - 0.5) * 4.0 - vec3.x * 8.0;
        double d2 = this.getY() + (double)(this.random.nextInt(8) - 4) - vec3.y * 8.0;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5) * 4.0 - vec3.z * 8.0;
        return this.teleport(d1, d2, d3);
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SummonWolfPackSpell spell = this.getOrCreateSpell();
        if (spell != null) {
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.THICK_FUR) && SpellUtil.isDark((Level) level, this.blockPosition())) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttributeValue(Attributes.MAX_HEALTH) * 1.5);
                this.getAttribute(Attributes.ARMOR).setBaseValue(5);
                this.getAttribute(SBAttributes.MAGIC_RESIST).setBaseValue(this.getAttributeBaseValue(SBAttributes.MAGIC_RESIST) * 1.5);
            }
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

}
