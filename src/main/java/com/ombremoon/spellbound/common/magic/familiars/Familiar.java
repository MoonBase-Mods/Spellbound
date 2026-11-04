package com.ombremoon.spellbound.common.magic.familiars;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.SpellEvent;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.main.CommonClass;
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
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public abstract class Familiar<T extends LivingEntity> {
    private static final Random RAND = new Random();
    public static final float FAMILIAR_LEVEL_DAMAGE_MULTIPLIER = 0.5F;
    public static final float PATH_LEVEL_DAMAGE_MULTIPLIER = 0.5F;
    public static final float HURT_XP_MODIFIER = 0.5F;

    private LivingEntity owner;
    private List<FamiliarAffinity> OWNER_ATTRIBUTE_MODIFIERS = new ArrayList<>();
    private List<FamiliarAffinity> FAMILIAR_ATTRIBUTE_MODIFIERS = new ArrayList<>();
    protected int rebirths;
    protected int bond;

    public Familiar(int bond, int rebirths) {
        this.bond = bond;
        this.rebirths = rebirths;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    /**
     * Get a Random to use for any probabilities
     * @return a Random instance
     */
    public Random getRandom() {
        return RAND;
    }

    /**
     * Override to apply a set of attribute modifiers to the familiar entity, this is called before onSpawn and inside onBondUp and onRebirth
     * @param handler The familiar handler
     * @param rebirths number of rebirths
     * @param bond the current bond
     * @return Attribute modifiers to apply
     */
    public List<FamiliarAffinity> modifyFamiliarAttributes(LivingEntity familiar, FamiliarHandler handler, int rebirths, int bond) {
        return new ArrayList<>();
    }

    /**
     * Override to apply a set of attribute modifiers to the familiars owner, this is called before onSpawn and inside onBondUp and onRebirth
     * @param handler The familiar handler
     * @param rebirths number of familiar rebirths
     * @param bond the current familiar bond
     * @return Attribute modifiers to apply
     */
    public List<FamiliarAffinity> modifyOwnerAttributes(LivingEntity owner, FamiliarHandler handler, int rebirths, int bond) {
        return new ArrayList<>();
    }

    public FamiliarAffinity addAttributeModifier(LivingEntity target, FamiliarAffinity affinity, Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
        ResourceLocation id = affinity.location().withSuffix(attribute.getKey().location().getPath());
        addSkillBuff(target,
                affinity,
                id,
                BuffCategory.BENEFICIAL,
                SkillBuff.ATTRIBUTE_MODIFIER,
                new ModifierData(attribute, new AttributeModifier(id, amount, operation)));

        return affinity;
    }

    /**
     * Called when the familiar is first summoned, attribute modifiers are already applied at this point
     * @param handler The familiar handler
     * @param spawnPos The position the entity was spawned
     */
    public void onSpawn(FamiliarHandler handler, BlockPos spawnPos) {
    }

    /**
     * Called when the familiar is removed from the world
     * @param handler The familiar handler
     * @param removePos The position the entity was when removed
     */
    public void onRemove(FamiliarHandler handler, BlockPos removePos) {
        for (FamiliarAffinity affinity : this.OWNER_ATTRIBUTE_MODIFIERS) {
            removeSkillBuff(getOwner(), affinity);
        }
    }

    /**
     * Called whenever {@link #shouldTick(FamiliarHandler, int)} returns true
     * @param handler The familiar handler
     * @param tickCount the entities tickcount
     */
    public void tick(FamiliarHandler handler, int tickCount) {

    }

    /**
     * Determines if the entity should tick
     * @param handler The familiar handler
     * @param tickCount the current tick count
     * @return true if should tick, false otherwise, default false
     */
    public boolean shouldTick(FamiliarHandler handler, int tickCount) {
        return false;
    }

    /**
     * Called when the familiars bond increases, by default refreshed attributes and heals the entity to full
     * @param handler The familiar handler
     * @param oldLevel The bond before increasing
     * @param newLevel The bond after increasing
     */
    public void onBondUp(FamiliarHandler handler, int oldLevel, int newLevel) {
        refreshAttributes(handler);
        handler.getActiveEntity().setHealth(handler.getActiveEntity().getMaxHealth());
    }

    /**
     * Called when the player rebirths the familiar, by default refreshes attributes and heals entity to full
     * @param handler The familiar handler
     * @param rebirths current number of rebirths
     */
    public void onRebirth(FamiliarHandler handler, int rebirths) {
        refreshAttributes(handler);
        handler.getActiveEntity().setHealth(handler.getActiveEntity().getMaxHealth());
    }

    /**
     * Called when an affinity comes off cooldown
     * @param handler The familiar handler
     * @param affinity The affinity off cooldown
     */
    public void onAffinityOffCooldown(FamiliarHandler handler, FamiliarAffinity affinity) {

    }

    /**
     * Removes current attribute modifiers that were defined in {@link #modifyFamiliarAttributes(FamiliarHandler, int, int)} and {@link #modifyOwnerAttributes(FamiliarHandler, int, int)} and adds them fresh
     * @param handler The familiar handler
     */
    public final void refreshAttributes(FamiliarHandler handler) {
        for (FamiliarAffinity affinity : this.OWNER_ATTRIBUTE_MODIFIERS) {
            removeSkillBuff(handler.getOwner(), affinity);
        }
        modifyOwnerAttributes(handler.getOwner(), handler, handler.getRebirths(handler.getSelectedFamiliar()), handler.getLevelForFamiliar(handler.getSelectedFamiliar()));

        for (FamiliarAffinity affinity : this.FAMILIAR_ATTRIBUTE_MODIFIERS) {
            removeSkillBuff(handler.getActiveEntity(), affinity);
        }
        modifyFamiliarAttributes(handler.getActiveEntity(), handler, handler.getRebirths(handler.getSelectedFamiliar()), handler.getLevelForFamiliar(handler.getSelectedFamiliar()));
    }

    /**
     * Returns the owner of the familiar
     * @return The owner entity
     */
    public LivingEntity getOwner() {
        return owner;
    }

    public int getRebirths() {
        return rebirths;
    }

    public int getBond() {
        return bond;
    }

    /**
     * Puts a given affinity on cooldown
     * @param handler The familiar handler
     * @param affinity The affinity to put on cooldown
     */
    public void useAffinity(FamiliarHandler handler, FamiliarAffinity affinity) {
        handler.putSkillOnCooldown(affinity);
    }

    /**
     * Checks if an affnity is unlocked, belongs to the current familiar and isnt on cooldown
     * @param handler The familiar handler
     * @param affinity The affinity to check
     * @return true if ready, false otherwise
     */
    public boolean hasAffinity(FamiliarHandler handler, FamiliarAffinity affinity) {
        return getBond() >= affinity.getRequiredBond()
                && handler.getSelectedFamiliar().getAffinities().contains(affinity)
                && handler.isSkillReady(affinity);
    }

    /**
     * Create a new attribute modifier that only applies if required affinity is available
     * @param modifierName suffix to append the modifiers name
     * @param handler The familiar handler
     * @param reqAffinity The affinity needed for it to apply
     * @param amount The amount to modify attribute by
     * @param op The AttributeModifier Operation
     * @return AttributeModifier instance
     */
    public AttributeModifier affinityModifier(String modifierName, FamiliarHandler handler, FamiliarAffinity reqAffinity, double amount, AttributeModifier.Operation op){
        return new AttributeModifier(reqAffinity.location().withSuffix("." + modifierName), hasAffinity(handler, reqAffinity) ? amount : 0D, op);
    }

    /**
     * Adds a spell event listener to the summoner, These should be removed when the familiar is discarded inside {@link #onRemove(FamiliarHandler, BlockPos)}
     * @param handler The familiar handler
     * @param spellEvent The event to add a listener for
     * @param identifier Idenfitier for the spell event
     * @param consumer the event callback
     * @param <T> The spell event
     */
    public <T extends SpellEvent> void addOwnerEventListener(FamiliarHandler handler, SpellEventListener.IEvent<T> spellEvent, ResourceLocation identifier, Consumer<T> consumer) {
        handler.getSpellHandler().getListener().addListener(
                spellEvent,
                identifier,
                consumer
        );
    }

    /**
     * Adds a spell event listener to the familiar, These should be removed when the familiar is discarded inside {@link #onRemove(FamiliarHandler, BlockPos)}
     * @param handler The familiar handler
     * @param spellEvent The event to add a listener for
     * @param identifier Idenfitier for the spell event
     * @param consumer the event callback
     * @param <T> The spell event
     */
    public <T extends SpellEvent> void addFamiliarEventListener(FamiliarHandler handler, SpellEventListener.IEvent<T> spellEvent, ResourceLocation identifier, Consumer<T> consumer) {
        SpellUtil.getSpellHandler(handler.getActiveEntity()).getListener().addListener(
                spellEvent,
                identifier,
                consumer
        );
    }

    /**
     * Adds a spell event listener to summoner and familiar, These should be removed when the familiar is discarded inside {@link #onRemove(FamiliarHandler, BlockPos)}
     * @param handler The familiar handler
     * @param spellEvent The event to add a listener for
     * @param identifier Idenfitier for the spell event
     * @param consumer the event callback
     * @param <T> The spell event
     */
    public <T extends SpellEvent> void addEventListener(FamiliarHandler handler, SpellEventListener.IEvent<T> spellEvent, ResourceLocation identifier, Consumer<T> consumer) {
        addOwnerEventListener(handler, spellEvent, identifier, consumer);
        addFamiliarEventListener(handler, spellEvent, identifier, consumer);
    }

    /**
     * Removes a spell event listener
     * @param handler The familiar handler
     * @param identifier Identifier of the listener to remove
     */
    public void removeOwnerEventListener(FamiliarHandler handler, ResourceLocation identifier) {
        handler.getSpellHandler().getListener().removeListener(identifier);
    }

    /**
     * Removes a spell event listener
     * @param handler The familiar handler
     * @param identifier Identifier of the listener to remove
     */
    public void removeFamiliarEventListener(FamiliarHandler handler, ResourceLocation identifier) {
        SpellUtil.getSpellHandler(handler.getActiveEntity()).getListener().removeListener(identifier);
    }

    public void removeEventListener(FamiliarHandler handler, ResourceLocation identifier) {
        removeOwnerEventListener(handler, identifier);
        removeFamiliarEventListener(handler, identifier);
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
    public <T> void addSkillBuff(LivingEntity livingEntity, FamiliarAffinity affinity, ResourceLocation buffName, BuffCategory buffCategory, SkillBuff.BuffObject<T> buffObject, T skillObject, int duration) {
        if (livingEntity.level().isClientSide || checkForCounterMagic(livingEntity) && buffCategory == BuffCategory.HARMFUL) return;
        SkillBuff<T> skillBuff = new SkillBuff<>(affinity, buffName, buffCategory, buffObject, skillObject);
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
    public <T> void addSkillBuff(LivingEntity livingEntity, FamiliarAffinity affinity, ResourceLocation buffName, BuffCategory buffCategory, SkillBuff.BuffObject<T> buffObject, T skillObject) {
        this.addSkillBuff(livingEntity, affinity, buffName, buffCategory, buffObject, skillObject, -1);
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
     * Hurts the entity entity, taking path level, potency, and magic resistance into account. Suitable for modded damage types.
     * @param handler familiar handler
     * @param targetEntity The hurt entity
     * @param damageType The damage type
     * @param hurtAmount The amount of damage the entity takesW
     * @return Whether the entity takes damage or not
     */
    private boolean hurt(FamiliarHandler handler, LivingEntity targetEntity, ResourceKey<DamageType> damageType, float hurtAmount) {
        if (!SpellUtil.CAN_ATTACK_ENTITY.test(handler.getOwner(), targetEntity))
            return false;

        float damageAfterResistance = this.getDamageAfterResistances(handler.getOwner(), targetEntity, getBond(), damageType, hurtAmount);
        boolean flag = targetEntity.hurt(SpellUtil.damageSource(handler.getLevel(), damageType, handler.getOwner(), handler.getActiveEntity()), damageAfterResistance);
        if (flag) handler.awardBond(handler.getSelectedFamiliar(), calculateHurtXP(damageAfterResistance));
        if (flag && !handler.getLevel().isClientSide) {
            targetEntity.setLastHurtByMob(handler.getActiveEntity());
            handler.getOwner().setLastHurtMob(targetEntity);
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
