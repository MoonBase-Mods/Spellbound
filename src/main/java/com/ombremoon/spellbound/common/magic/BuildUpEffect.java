package com.ombremoon.spellbound.common.magic;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBEffects;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

public enum BuildUpEffect implements StringRepresentable {
    FIRE("fire", SpellPath.FIRE, SBEffects.COMBUST, SBAttributes.FIRE_SPELL_RESIST, SBDamageTypes.RUIN_FIRE, 20),
    FROST("frost", SpellPath.FROST, SBEffects.FROZEN, SBAttributes.FROST_SPELL_RESIST, SBDamageTypes.RUIN_FROST, 60),
    SHOCK("shock", SpellPath.SHOCK, SBEffects.DISCHARGE, SBAttributes.SHOCK_SPELL_RESIST, SBDamageTypes.RUIN_SHOCK, 100),
    DISEASE("disease", SpellPath.DARK_DIVINE, SBEffects.DISEASE, SBAttributes.DISEASE_RESIST, SBDamageTypes.DISEASE, 100),
    BLEED("bleed", SpellPath.BLOOD, SBEffects.BLEED, SBAttributes.BLOOD_LOSS_RESIST, SBDamageTypes.BLEED, 100);

    public static final StringRepresentable.EnumCodec<BuildUpEffect> CODEC = StringRepresentable.fromEnum(BuildUpEffect::values);
    private final String name;
    private final SpellPath path;
    private final Holder<MobEffect> mobEffect;
    private final Holder<Attribute> resistance;
    private final ResourceKey<DamageType> damageType;
    private final int duration;

    BuildUpEffect(String name, SpellPath path, Holder<MobEffect> mobEffect, Holder<Attribute> resistance, ResourceKey<DamageType> damageType, int duration) {
        this.name = name;
        this.path = path;
        this.mobEffect = mobEffect;
        this.resistance = resistance;
        this.damageType = damageType;
        this.duration = duration;
    }

    public SpellPath getPath() {
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
     * @return The resistance the entity has to the effect (-100 to 100)
     */
    public float getEntityResistance(LivingEntity entity) {
        return entity.getAttribute(resistance) != null ? (float) entity.getAttribute(resistance).getValue() : 0.0F;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}