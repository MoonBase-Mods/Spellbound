package com.ombremoon.spellbound.common.magic;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class EffectManager implements INBTSerializable<CompoundTag>, Loggable {
    protected static final float PATH_BUILD_UP_MODIFIER = 0.01F;
    protected static final float JUDGEMENT_MODIFIER = 0.5F;
    public static final int MAX_JUDGEMENT = 100;
    public static final int MIN_JUDGEMENT = -100;
    private LivingEntity livingEntity;
    private final Map<Effect, Float> buildUp = new HashMap<>();
    private int judgement;

    /**
     * Initialises the effect handler
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
    public void incrementBuildEffects(Effect effect, float amount) {
        Float progress = this.buildUp.get(effect);
        float buildUpAmount = this.calculateBuildUp(effect, amount);
        if (progress != null) {
            progress = Math.clamp(progress + buildUpAmount, 0.0F, 100.0F);
        } else {
            progress = buildUpAmount;
        }

        this.buildUp.put(effect, progress);
        if (progress >= 100.0F) {
            tryApplyEffect(effect);
            this.buildUp.put(effect, 0.0F);
        }
    }

    private float calculateBuildUp(Effect effect, float amount) {
        var skills = SpellUtil.getSkills(this.livingEntity);
        float resistance = 1.0F - effect.getEntityResistance(this.livingEntity);

        SpellPath path = effect.getPath();
        float pathAmount = path != null ? 1.0F + (PATH_BUILD_UP_MODIFIER * skills.getPathLevel(path)) : 1.0F;
        float ruinPathAmount = 1.0F + (PATH_BUILD_UP_MODIFIER * skills.getPathLevel(SpellPath.RUIN));
        return amount * 2.0F * resistance * pathAmount * ruinPathAmount;
    }

    public int getJudgement() {
        return this.judgement;
    }

    public void giveJudgement(int amount) {
        this.judgement = Math.clamp(this.judgement + amount, MIN_JUDGEMENT, MAX_JUDGEMENT);
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
    public float getBuildUp(Effect effect) {
        if (!isInitialised()) return 0F;

        Float amount = buildUp.get(effect);
        return amount != null ? buildUp.get(effect) : 0F;
    }

    /**
     * Clears all of an effects buildup and removes the effect if present.
     * @param effect the effect to clear
     */
    public void clearEffect(Effect effect) {
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
     * @param tickCount the tick count of the entity currently ticking
     */
    @ApiStatus.Internal
    public void tick(int tickCount) {
        if (livingEntity == null)
            throw new RuntimeException("Status Effects not initialised.");

        if (tickCount % 40 == 0) {
            this.buildUp.forEach((effect, amount) -> {
                if (amount > 0)
                    this.buildUp.replace(effect, amount - 1);
            });
        }
    }

    /**
     * Attempts to add the mob effect tied to an effect to the player
     * @param effect the effect that has reached 100 buildup
     */
    private void tryApplyEffect(Effect effect) {
        if (effect.getEffect() == null)
            return;

        livingEntity.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration()));
    }

    public Effect getEffectFromDamageType(ResourceKey<DamageType> damageType) {
        if (damageType == SBDamageTypes.RUIN_FIRE) {
            return Effect.FIRE;
        } else if (damageType == SBDamageTypes.RUIN_SHOCK) {
            return Effect.SHOCK;
        } else if (damageType == SBDamageTypes.RUIN_FROST) {
            return Effect.FROST;
        } else {
            //ADD DAMAGE BUILD UP EVENT
            return null;
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (Effect effect : this.buildUp.keySet()) {
            tag.putFloat(effect.name(), this.buildUp.get(effect));
        }
        tag.putInt("Judgement", this.judgement);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        for (Effect effect : Effect.values()) {
            if (tag.contains(effect.name())) {
                this.buildUp.put(effect, tag.getFloat(effect.name()));
            }
        }

        if (tag.contains("Judgement", 99))
            this.judgement = tag.getInt("Judgement");
    }

    public enum Effect {
        FIRE(SpellPath.FIRE, SBEffects.COMBUST, SBAttributes.FIRE_SPELL_RESIST, SBDamageTypes.RUIN_FIRE, 20),
        FROST(SpellPath.FROST, SBEffects.FROZEN, SBAttributes.FROST_SPELL_RESIST, SBDamageTypes.RUIN_FROST, 60),
        SHOCK(SpellPath.SHOCK, SBEffects.DISCHARGE, SBAttributes.SHOCK_SPELL_RESIST, SBDamageTypes.RUIN_SHOCK, 100),
        DISEASE(SpellPath.DARK_DIVINE, SBEffects.DISEASE, SBAttributes.DISEASE_RESIST, SBDamageTypes.DISEASE, 100);

        @Nullable
        private final SpellPath path;
        private final Holder<MobEffect> mobEffect;
        private final Holder<Attribute> resistance;
        private final ResourceKey<DamageType> damageType;
        private final int duration;

        Effect(@Nullable SpellPath path, Holder<MobEffect> mobEffect, Holder<Attribute> resistance, ResourceKey<DamageType> damageType, int duration) {
            this.path = path;
            this.mobEffect = mobEffect;
            this.resistance = resistance;
            this.damageType = damageType;
            this.duration = duration;
        }

        public @Nullable SpellPath getPath() {
            return this.path;
        }

        /**
         * Gets the mob effect which is applied when this status reaches 100
         * @return The MobEffect applied when buildup is 100
         */
        public Holder<MobEffect> getEffect() {
            return this.mobEffect;
        }

        public int getDuration() {
            return this.duration;
        }

        /**
         * The attribute which affects the amount of buildup applied
         * @return the attribute used in buildup calculations
         */
        public Holder<Attribute> getResistance() {
            return this.resistance;
        }

        public ResourceKey<DamageType> getDamageType() {
            return this.damageType;
        }

        /**
         * Gets the value of the resistance attribute that an entity has for negating this effect build up
         * @param entity The entity to check resistance for
         * @return The resistance the entity has to the effect (-100 - 100)
         */
        public float getEntityResistance(LivingEntity entity) {
            return entity.getAttribute(resistance) != null ? (float) entity.getAttribute(resistance).getValue() : 0.0F;
        }
    }
}
