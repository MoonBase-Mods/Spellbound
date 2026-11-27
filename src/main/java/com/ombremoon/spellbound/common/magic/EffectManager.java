package com.ombremoon.spellbound.common.magic;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.magic.effects.MagicEffectInstance;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectManager implements INBTSerializable<CompoundTag>, Loggable {
    protected static final float PATH_BUILD_UP_MODIFIER = 0.01F;
    protected static final float JUDGEMENT_MODIFIER = 0.5F;
    public static final int MAX_JUDGEMENT = 100;
    public static final int MIN_JUDGEMENT = -100;
    private LivingEntity livingEntity;
    private final Map<BuildUpEffect, Float> buildUp = new HashMap<>();
    private final Map<BuildUpEffect, Integer> buildUpTick = new HashMap<>();
    private final List<MagicEffectInstance> magicEffects = new ObjectArrayList<>();
    private float judgement;

    public void sync() {
        if (this.livingEntity instanceof Player player)
            PayloadHandler.syncEffectsToClient(player);
    }

    /**
     * Initializes the effect handler
     * @param self the entity to attach the effect handler to
     */
    public void init(LivingEntity self) {
        this.livingEntity = self;
    }

    /**
     * Checks if the effect handler has been initialised
     * @return true if initialised, false otherwise
     */
    public boolean isInitialised() { return livingEntity != null; }

    /**
     * Checks if the entity is rooted (cant move)
     * @param entity The entity to check if rooted
     * @return true if they are rooted (cant move), false otherwise
     */
    public static boolean isRooted(LivingEntity entity) {
        return entity.hasEffect(SBEffects.ROOTED)
                || isStunned(entity);
    }

    /**
     * Checks if the entity is silenced (cant cast spells)
     * @param entity The entity to check if its silenced
     * @return true if silenced (cant cast spells), false otherwise
     */
    public static boolean isSilenced(LivingEntity entity) {
        return entity.hasEffect(SBEffects.SILENCED)
                || isStunned(entity)
                || entity.hasEffect(SBEffects.DISCHARGE);
    }

    /**
     * Checks if the entity is stunned (cant move/cast spells/use items/break blocks/turn)
     * @param entity The entity to check if its stunned
     * @return true if stunned, false otherwise
     */
    public static boolean isStunned(LivingEntity entity) {
        return entity.hasEffect(SBEffects.STUNNED)
                || entity.hasEffect(SBEffects.FROZEN)
                || entity.hasEffect(SBEffects.SLEEP);
    }

    public double getMagicResistance() {
        return this.livingEntity.getAttribute(SBAttributes.MAGIC_RESIST) != null ? this.livingEntity.getAttributeValue(SBAttributes.MAGIC_RESIST) : 0;
    }

    /**
     * Increments a given status effect by a set amount, will apply the effect upon reaching 100
     * @param effect The type of effect to buildup
     * @param amount amount to increase the build up by
     */
    public void incrementBuildEffects(@Nullable LivingEntity source, BuildUpEffect effect, float amount) {
        if (this.livingEntity.hasEffect(effect.getEffect()))
            return;

        Float progress = this.buildUp.get(effect);
        float buildUpAmount = this.calculateBuildUp(source, effect, amount);
        if (progress != null) {
            progress = Math.clamp(progress + buildUpAmount, 0.0F, 100.0F);
        } else {
            progress = buildUpAmount;
        }

        this.buildUp.put(effect, progress);
        this.buildUpTick.put(effect, this.livingEntity.tickCount);
        if (progress >= 100.0F) {
            tryApplyEffect(effect);
            this.buildUp.put(effect, 0.0F);
        }
    }

    private float calculateBuildUp(@Nullable LivingEntity source, BuildUpEffect effect, float amount) {
        float resistance = 1.0F - effect.getEntityResistance(this.livingEntity);
        if (source == null)
            return amount * 2.0F * resistance;

        var skills = SpellUtil.getSkills(source);
        SpellPath path = effect.getPath();
        float pathAmount = 1.0F + (PATH_BUILD_UP_MODIFIER * skills.getPathLevel(path));
        float ruinPathAmount = 1.0F;
//        if (path.isRuin())
//            ruinPathAmount += PATH_BUILD_UP_MODIFIER * skills.getPathLevel(SpellPath.RUIN);

        return amount * 1.5F * resistance * pathAmount * ruinPathAmount;
    }

    public void addMagicEffect(EffectHolder effect, @Nullable Entity source) {
        if (!livingEntity.level().isClientSide) {
            MagicEffectInstance instance = new MagicEffectInstance(effect);
            instance.initializeEffect(source);
            this.magicEffects.add(instance);
        }
    }

    public float getJudgement() {
        return this.judgement;
    }

    public void giveJudgement(float amount) {
        this.judgement = Math.clamp(this.judgement + amount, MIN_JUDGEMENT, MAX_JUDGEMENT);
        sync();
    }

    public float getJudgementFactor(boolean isNegative) {
        int threshold = isNegative ? MIN_JUDGEMENT : MAX_JUDGEMENT;
        return Math.max(0, 1 + JUDGEMENT_MODIFIER * judgement / threshold);
    }

    /**
     * Gets the current build up for a given status effect
     * @param effect the effect to check the build up for
     * @return current buildup (0-100)
     */
    public float getBuildUp(BuildUpEffect effect) {
        if (!isInitialised()) return 0F;

        Float amount = buildUp.get(effect);
        return amount != null ? buildUp.get(effect) : 0F;
    }

    /**
     * Clears all of an effects buildup and removes the effect if present.
     * @param effect the effect to clear
     */
    public void clearEffect(BuildUpEffect effect) {
        buildUp.put(effect, 0F);
        livingEntity.removeEffect(effect.getEffect());
    }

    /**
     * Clears all build ups progress and removes all status effects
     */
    public void clearAll() {
        buildUp.keySet().forEach(this::clearEffect);
    }

    /**
     * Ticks the build up effects, decreasing the progress by 1 per second
     */
    @ApiStatus.Internal
    public void tick() {
        if (livingEntity == null)
            throw new RuntimeException("Status Effects not initialised.");

        this.buildUp.forEach((effect, amount) -> {
            int i = this.buildUpTick.computeIfAbsent(effect, effect1 -> livingEntity.tickCount);
            if (this.livingEntity.tickCount - i > 60 && this.livingEntity.tickCount % 20 == 0 && amount > 0)
                this.buildUp.replace(effect, Math.max(0, amount - 5));
        });

        if (!this.livingEntity.level().isClientSide) {
            this.magicEffects.removeIf(effectHolder -> !effectHolder.attemptToActivateOrRemoveEffect((ServerLevel) this.livingEntity.level(), 0, this.livingEntity, this.livingEntity.blockPosition(), null));
        }
    }

    /**
     * Attempts to add the mob effect tied to an effect to the player
     * @param effect the effect that has reached 100 buildup
     */
    private void tryApplyEffect(BuildUpEffect effect) {
        if (effect.getEffect() == null)
            return;

        livingEntity.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration()));
    }

    public BuildUpEffect getEffectFromDamageType(ResourceKey<DamageType> damageType) {
        if (damageType == SBDamageTypes.RUIN_FIRE) {
            return BuildUpEffect.FIRE;
        } else if (damageType == SBDamageTypes.RUIN_SHOCK) {
            return BuildUpEffect.SHOCK;
        } else if (damageType == SBDamageTypes.RUIN_FROST) {
            return BuildUpEffect.FROST;
        } else if (damageType == SBDamageTypes.BLEED) {
            return BuildUpEffect.BLEED;
        } else {
            //ADD DAMAGE BUILD UP EVENT
            return null;
        }
    }

    public void doPostAttackEffects(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        Level level = this.livingEntity.level();
        if (!level.isClientSide) {
            for (MagicEffectInstance effect : this.magicEffects) {
                effect.doPostAttackEffects(target, this.livingEntity, 0, this.livingEntity.blockPosition(), null, event.getSource());
            }
        }
    }

    public void doPreAttackEffects(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Level level = this.livingEntity.level();
        if (!level.isClientSide) {
            for (MagicEffectInstance effect : this.magicEffects) {
                effect.doPreAttackEffects(target, this.livingEntity, 0, this.livingEntity.blockPosition(), null, event.getContainer());
            }
        }
    }

    public void doPreDamageEffects(LivingDamageEvent.Pre event) {
        Entity source = event.getSource().getEntity();
        Level level = this.livingEntity.level();
        if (!level.isClientSide) {
            for (MagicEffectInstance effect : this.magicEffects) {
                effect.doPreDamageEffects(this.livingEntity, source, 0, this.livingEntity.blockPosition(), null, event.getContainer());
            }
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (BuildUpEffect effect : this.buildUp.keySet()) {
            tag.putFloat(effect.name(), this.buildUp.get(effect));
        }
        tag.putFloat("Judgement", this.judgement);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        for (BuildUpEffect effect : BuildUpEffect.values()) {
            if (tag.contains(effect.name())) {
                this.buildUp.put(effect, tag.getFloat(effect.name()));
            }
        }

        this.judgement = tag.getFloat("Judgement");
    }
}
