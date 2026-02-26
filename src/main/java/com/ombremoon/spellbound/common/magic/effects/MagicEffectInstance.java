package com.ombremoon.spellbound.common.magic.effects;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class MagicEffectInstance {
    private static final Logger LOGGER = Constants.LOG;
    private final EffectHolder effect;
    private int tickCount;
    private UUID effectSourceId;
    private LootContext context;

    public MagicEffectInstance(EffectHolder effect) {
        this.effect = effect;
    }

    public void initializeEffect(@Nullable Entity source) {
        this.tickCount = 0;
        this.effectSourceId = source != null ? source.getUUID() : null;
    }

    public boolean attemptToActivateOrRemoveEffect(ServerLevel level, int tier, LivingEntity caster, BlockPos centerPos, @Nullable Multiblock.MultiblockPattern pattern) {
        if (this.tickCount >= this.effect.effectDuration()) {
            this.effect.effect().onDeactivated(level, tier, level.getEntity(this.effectSourceId), caster, centerPos, pattern);
            return false;
        }

        if (!this.effect.hasComponents() && this.isValid(level, tier, caster, centerPos, pattern)) {
            this.effect.effect().onActivated(level, tier, level.getEntity(this.effectSourceId), caster, centerPos, pattern);
        }

        this.tickCount++;
        return true;
    }

    private boolean isValid(ServerLevel level, int tier, LivingEntity caster, BlockPos centerPos, @Nullable Multiblock.MultiblockPattern pattern) {
        if (!this.effect.tickProvider().shouldTick(this.tickCount))
            return false;

        return this.isValidContext(level, tier, caster, centerPos, pattern, null);
    }

    private boolean isValidContext(ServerLevel level, int tier, LivingEntity caster, BlockPos centerPos, @Nullable Multiblock.MultiblockPattern pattern, @Nullable DamageSource damageSource) {
        this.context = this.effect.effect().createContext(level, tier, level.getEntity(this.effectSourceId), caster, centerPos, pattern, damageSource);
        return this.effect.requirement().map(condition -> condition.test(this.context)).orElse(true);
    }

    public boolean withinValidEffectRange(LivingEntity entity, BlockPos origin) {
        Optional<RangeProvider> optional = this.effect.range();
        if (optional.isEmpty())
            return true;

        RangeProvider range = optional.get();
        return entity.distanceToSqr(Vec3.atCenterOf(origin)) <= range.maxRadius() * range.maxRadius();
    }

    public void doPostAttackEffects(LivingEntity target, Entity source, int tier, BlockPos blockPos, Multiblock.MultiblockPattern pattern, DamageSource damageSource) {
        if (this.effect.hasComponent(SBData.POST_ATTACK)) {
            MagicEffect effect = this.effect.effect();
            EnchantmentTarget enchantmentTarget = this.effect.getComponentValue(SBData.POST_ATTACK);
            LivingEntity entity = enchantmentTarget != EnchantmentTarget.VICTIM && source instanceof LivingEntity living ? living : target;
            if (this.withinValidEffectRange(entity, blockPos) && this.isValidContext((ServerLevel) target.level(), tier, entity, blockPos, pattern, damageSource)) {
                effect.onActivated((ServerLevel) target.level(), tier, source, entity, blockPos, pattern);
                effect.onPostAttack((ServerLevel) target.level(), tier, source, entity, blockPos, pattern, damageSource);
            }
        }
    }

    public void doPreAttackEffects(LivingEntity target, Entity source, int tier, BlockPos blockPos, Multiblock.MultiblockPattern pattern, DamageContainer container) {
        if (this.effect.hasComponent(SBData.PRE_ATTACK)) {
            MagicEffect effect = this.effect.effect();
            EnchantmentTarget enchantmentTarget = this.effect.getComponentValue(SBData.PRE_ATTACK);
            LivingEntity entity = enchantmentTarget != EnchantmentTarget.VICTIM && source instanceof LivingEntity living ? living : target;
            if (this.withinValidEffectRange(entity, blockPos) && this.isValidContext((ServerLevel) target.level(), tier, entity, blockPos, pattern, container.getSource())) {
                effect.onActivated((ServerLevel) target.level(), tier, source, entity, blockPos, pattern);
                effect.onPreAttack((ServerLevel) target.level(), tier, source, entity, blockPos, pattern, container);
            }
        }
    }

    public void doPreDamageEffects(LivingEntity target, Entity source, int tier, BlockPos blockPos, Multiblock.MultiblockPattern pattern, DamageContainer container) {
        if (this.effect.hasComponent(SBData.PRE_DAMAGE)) {
            MagicEffect effect = this.effect.effect();
            EnchantmentTarget enchantmentTarget = this.effect.getComponentValue(SBData.PRE_DAMAGE);
            LivingEntity entity = enchantmentTarget != EnchantmentTarget.VICTIM && source instanceof LivingEntity living ? living : target;
            if (this.withinValidEffectRange(entity, blockPos) && this.isValidContext((ServerLevel) target.level(), tier, entity, blockPos, pattern, container.getSource())) {
                effect.onActivated((ServerLevel) target.level(), tier, source, entity, blockPos, pattern);
                effect.onPreDamage((ServerLevel) target.level(), tier, source, entity, blockPos, pattern, container);
            }
        }
    }

    public boolean hasSource() {
        return this.effectSourceId != null;
    }

    public EffectHolder getEffect() {
        return this.effect;
    }
}
