package com.ombremoon.spellbound.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record DamageInstance(ResourceKey<DamageType> damageType, float amount, Optional<TagKey<EntityType<?>>> exclusiveTo) {
    public static final Codec<DamageInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(DamageInstance::damageType),
                    Codec.FLOAT.fieldOf("amount").forGetter(DamageInstance::amount),
                    TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("exclusive_to").forGetter(DamageInstance::exclusiveTo)
            ).apply(instance, DamageInstance::new)
    );

    public static DamageInstance of(ResourceKey<DamageType> damageType, float amount, TagKey<EntityType<?>> exclusiveTo) {
        return new DamageInstance(damageType, amount, Optional.of(exclusiveTo));
    }

    public static DamageInstance of(ResourceKey<DamageType> damageType, float amount) {
        return new DamageInstance(damageType, amount, Optional.empty());
    }
}
