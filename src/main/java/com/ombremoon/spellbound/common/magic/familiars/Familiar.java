package com.ombremoon.spellbound.common.magic.familiars;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.buff.events.SpellEvent;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Consumer;

public abstract class Familiar<T extends LivingEntity> {
    private static final Random RAND = new Random();
    public static final float FAMILIAR_LEVEL_DAMAGE_MULTIPLIER = 0.5F;
    public static final float PATH_LEVEL_DAMAGE_MULTIPLIER = 0.5F;
    public static final float HURT_XP_MODIFIER = 0.5F;

    private LivingEntity owner;
    private Multimap<Holder<Attribute>, AttributeModifier> familiarModifiers;
    private Multimap<Holder<Attribute>, AttributeModifier> ownerModifiers;

    /**
     * Get a Random to use for any probabilities
     * @return a Random instance
     */
    public Random getRandom() {
        return RAND;
    }

    /**
     * Override to apply a set of attribute modifiers to the familiar entity, this is called before onSpawn and inside onBondUp and onRebirth
     * @param context The familiar context instance
     * @param rebirths number of rebirths
     * @param bond the current bond
     * @return Attribute modifiers to apply
     */
    public Multimap<Holder<Attribute>, AttributeModifier> modifyFamiliarAttributes(FamiliarContext context, int rebirths, int bond) {
        return ArrayListMultimap.create();
    }

    /**
     * Override to apply a set of attribute modifiers to the familiars owner, this is called before onSpawn and inside onBondUp and onRebirth
     * @param context The familiar context instance
     * @param rebirths number of familiar rebirths
     * @param bond the current familiar bond
     * @return Attribute modifiers to apply
     */
    public Multimap<Holder<Attribute>, AttributeModifier> modifyOwnerAttributes(FamiliarContext context, int rebirths, int bond) {
        return ArrayListMultimap.create();
    }

    /**
     * Called when the familiar is first summoned, attribute modifiers are already applied at this point
     * @param context The familiar context instance
     * @param spawnPos The position the entity was spawned
     */
    public void onSpawn(FamiliarContext context, BlockPos spawnPos) {
        this.owner = context.getOwner();
    }

    /**
     * Called when the familiar is removed from the world
     * @param context The familiar context instance
     * @param removePos The position the entity was when removed
     */
    public void onRemove(FamiliarContext context, BlockPos removePos) {
        context.getOwner().getAttributes().removeAttributeModifiers(ownerModifiers);
    }

    /**
     * Called whenever {@link #shouldTick(FamiliarContext, int)} returns true
     * @param context The familiar context
     */
    public void tick(FamiliarContext context) {

    }

    /**
     * Determines if the entity should tick
     * @param context The familiar context
     * @param tickCount the current tick count
     * @return true if should tick, false otherwise, default false
     */
    public boolean shouldTick(FamiliarContext context, int tickCount) {
        return false;
    }

    /**
     * Called when the familiars bond increases, by default refreshed attributes and heals the entity to full
     * @param context The familiar context instance
     * @param oldLevel The bond before increasing
     * @param newLevel The bond after increasing
     */
    public void onBondUp(FamiliarContext context, int oldLevel, int newLevel) {
        refreshAttributes(context);
        context.getEntity().setHealth(context.getEntity().getMaxHealth());
    }

    /**
     * Called when the player rebirths the familiar, by default refreshes attributes and heals entity to full
     * @param context The familiar context instance
     * @param rebirths current number of rebirths
     */
    public void onRebirth(FamiliarContext context, int rebirths) {
        refreshAttributes(context);
        context.getEntity().setHealth(context.getEntity().getMaxHealth());
    }

    /**
     * Called when an affinity comes off cooldown
     * @param context The familiar context instance
     * @param affinity The affinity off cooldown
     */
    public void onAffinityOffCooldown(FamiliarContext context, FamiliarAffinity affinity) {

    }

    /**
     * Removes current attribute modifiers that were defined in {@link #modifyFamiliarAttributes(FamiliarContext, int, int)} and {@link #modifyOwnerAttributes(FamiliarContext, int, int)} and adds them fresh
     * @param context The familiar context instance
     */
    public final void refreshAttributes(FamiliarContext context) {
        if (this.familiarModifiers == null || this.familiarModifiers.isEmpty()) {
            this.familiarModifiers = modifyFamiliarAttributes(context, context.getRebirths(), context.getBond());
            context.getEntity().getAttributes().addTransientAttributeModifiers(this.familiarModifiers);
        } else {
            context.getEntity().getAttributes().removeAttributeModifiers(familiarModifiers);
            this.familiarModifiers = modifyFamiliarAttributes(context, context.getRebirths(), context.getBond());
            context.getEntity().getAttributes().addTransientAttributeModifiers(this.familiarModifiers);
        }


        if (this.ownerModifiers == null || this.ownerModifiers.isEmpty()) {
            this.ownerModifiers = modifyOwnerAttributes(context, context.getRebirths(), context.getBond());
            context.getOwner().getAttributes().addTransientAttributeModifiers(this.ownerModifiers);
        } else {
            context.getOwner().getAttributes().removeAttributeModifiers(ownerModifiers);
            this.ownerModifiers = modifyOwnerAttributes(context, context.getRebirths(), context.getBond());
            context.getEntity().getAttributes().addTransientAttributeModifiers(this.ownerModifiers);
        }

    }

    /**
     * Returns the owner of the familiar
     * @return The owner entity
     */
    public LivingEntity getOwner() {
        return owner;
    }

    /**
     * Puts a given affinity on cooldown
     * @param context The familiar context instance
     * @param affinity The affinity to put on cooldown
     */
    public void useAffinity(FamiliarContext context, FamiliarAffinity affinity) {
        context.getHandler().putSkillOnCooldown(affinity);
    }

    /**
     * Checks if an affnity is unlocked, belongs to the current familiar and isnt on cooldown
     * @param context The familiar context instance
     * @param affinity The affinity to check
     * @return true if ready, false otherwise
     */
    public boolean hasAffinity(FamiliarContext context, FamiliarAffinity affinity) {
        return context.getBond() >= affinity.getRequiredBond()
                && context.getHolder().getAffinities().contains(affinity)
                && context.getHandler().isSkillReady(affinity);
    }

    /**
     * Create a new attribute modifier that only applies if required affinity is available
     * @param modifierName suffix to append the modifiers name
     * @param context The familiar context instance
     * @param reqAffinity The affinity needed for it to apply
     * @param amount The amount to modify attribute by
     * @param op The AttributeModifier Operation
     * @return AttributeModifier instance
     */
    public AttributeModifier affinityModifier(String modifierName, FamiliarContext context, FamiliarAffinity reqAffinity, double amount, AttributeModifier.Operation op){
        return new AttributeModifier(reqAffinity.location().withSuffix("." + modifierName), hasAffinity(context, reqAffinity) ? amount : 0D, op);
    }

    /**
     * Adds a spell event listener, These should be removed when the familiar is discarded inside {@link #onRemove(FamiliarContext, BlockPos)}
     * @param context The familiar context instance
     * @param spellEvent The event to add a listener for
     * @param identifier Idenfitier for the spell event
     * @param consumer the event callback
     * @param <T> The spell event
     */
    public <T extends SpellEvent> void addEventListener(FamiliarContext context, SpellEventListener.IEvent<T> spellEvent, ResourceLocation identifier, Consumer<T> consumer) {
        context.getSpellHandler().getListener().addListener(
                spellEvent,
                identifier,
                consumer
        );
    }

    /**
     * Removes a spell event listener
     * @param context The familiar context instance
     * @param identifier Identifier of the listener to remove
     */
    public void removeEventListener(FamiliarContext context, ResourceLocation identifier) {
        context.getSpellHandler().getListener().removeListener(identifier);
    }

    /**
     * Adds a {@link SkillBuff} to a living entity for a specified amount of ticks.
     * @param livingEntity The living entity
     * @param affinity The affinity that activates the buff
     * @param buffCategory Whether it's a good, bad, or neutral buff
     * @param buffObject The type of buff: Mob Effect, Attribute Modifier, Spell Modifier, or Event Listener
     * @param skillObject The actual buff being applied to the entity
     * @param duration The length in ticks the buff persists
     * @param <T> The buff
     */
    public <T> void addSkillBuff(LivingEntity livingEntity, FamiliarAffinity affinity, BuffCategory buffCategory, SkillBuff.BuffObject<T> buffObject, T skillObject, int duration) {
        if (livingEntity.level().isClientSide || checkForCounterMagic(livingEntity) && buffCategory == BuffCategory.HARMFUL) return;
        SkillBuff<T> skillBuff = new SkillBuff<>(affinity, buffCategory, buffObject, skillObject);
        var handler = SpellUtil.getSpellHandler(livingEntity);
        handler.addSkillBuff(skillBuff, livingEntity, duration);
    }

    /**
     * Removes a skill buff
     * @param livingEntity The entity
     * @param affinity skill buff affinity to remove
     */
    public void removeSkillBuff(LivingEntity livingEntity, FamiliarAffinity affinity) {
        var handler = SpellUtil.getSpellHandler(livingEntity);
        var buffs = handler.getBuffs().stream().filter(skillBuff -> skillBuff.isSkill(affinity)).toList();
        this.removeSkillBuff(livingEntity, affinity, buffs.size());
    }

    /**
     * Removes a skill buff
     * @param livingEntity The entity
     * @param skill skill buff affinity to remove
     * @param iterations number of active buffs from that affinity
     */
    private void removeSkillBuff(LivingEntity livingEntity, FamiliarAffinity skill, int iterations) {
        if (livingEntity.level().isClientSide)
            return;

        for (int i = 0; i < iterations; i++) {
            var handler = SpellUtil.getSpellHandler(livingEntity);
            var optional = handler.getSkillBuff(skill);
            if (optional.isPresent()) {
                SkillBuff<?> skillBuff = optional.get();
                handler.removeSkillBuff(skillBuff);
            }
        }
    }

    /**
     * Adds a skill buff until removed
     * @param livingEntity The living entity
     * @param affinity The affinity that activates the buff
     * @param buffCategory Whether it's a good, bad, or neutral buff
     * @param buffObject The type of buff: Mob Effect, Attribute Modifier, Spell Modifier, or Event Listener
     * @param skillObject The actual buff being applied to the entity
     * @param <T> the buff
     */
    public <T> void addSkillBuff(LivingEntity livingEntity, FamiliarAffinity affinity, BuffCategory buffCategory, SkillBuff.BuffObject<T> buffObject, T skillObject) {
        this.addSkillBuff(livingEntity, affinity, buffCategory, buffObject, skillObject, -1);
    }

    /**
     * Checks for the counter magic effect
     * @param targetEntity entity to check
     * @return true if present, false otherwise
     */
    public boolean checkForCounterMagic(LivingEntity targetEntity) {
        return targetEntity.hasEffect(SBEffects.COUNTER_MAGIC) || targetEntity instanceof Player player && player.isCreative();
    }

    /**
     * Calculates the amount of XP gained from hurting an entity
     * @param amount The damage amount
     * @return The modified xp value
     */
    private float calculateHurtXP(float amount) {
        return amount * (1.0F + HURT_XP_MODIFIER);
    }

    /**
     * Hurts the target entity, taking path level, potency, and magic resistance into account. Suitable for modded damage types.
     * @param context context
     * @param targetEntity The hurt entity
     * @param damageType The damage type
     * @param hurtAmount The amount of damage the entity takesW
     * @return Whether the entity takes damage or not
     */
    private boolean hurt(FamiliarContext context, LivingEntity targetEntity, ResourceKey<DamageType> damageType, float hurtAmount) {
        if (!SpellUtil.CAN_ATTACK_ENTITY.test(context.getOwner(), targetEntity))
            return false;

        float damageAfterResistance = this.getDamageAfterResistances(context.getOwner(), targetEntity, context.getBond(), damageType, hurtAmount);
        boolean flag = targetEntity.hurt(SpellUtil.damageSource(context.getLevel(), damageType, context.getOwner(), context.getEntity()), damageAfterResistance);
        if (flag) context.getHandler().awardBond(context.getHolder(), calculateHurtXP(damageAfterResistance));
        if (flag && !context.getLevel().isClientSide) {
            targetEntity.setLastHurtByMob(context.getEntity());
            context.getOwner().setLastHurtMob(targetEntity);
        }
        return flag;
    }

    private float getDamageAfterResistances(LivingEntity owner, LivingEntity targetEntity, int bond, ResourceKey<DamageType> damageType, float damageAmount) {
        var effects = SpellUtil.getSpellEffects(targetEntity);
        float f = (float) (this.getModifiedDamage(owner, damageAmount, bond) * (1.0F - effects.getMagicResistance()));
        var effect = effects.getEffectFromDamageType(damageType);
        return effect != null ? f * (1.0F - effect.getEntityResistance(targetEntity)) : f;
    }

    public float getModifiedDamage(LivingEntity ownerEntity, float amount, int bond) {
        var skills = SpellUtil.getSkills(ownerEntity);
        var effects = SpellUtil.getSpellEffects(ownerEntity);
        float levelDamage = amount * (1.0F + FAMILIAR_LEVEL_DAMAGE_MULTIPLIER * bond);
        levelDamage *= 1 + PATH_LEVEL_DAMAGE_MULTIPLIER * ((float) skills.getPathLevel(SpellPath.SUMMONS) / 100);
        return potency(ownerEntity, levelDamage);
    }

    protected float potency(LivingEntity livingEntity, float initialAmount) {
        return initialAmount;
    }

}
