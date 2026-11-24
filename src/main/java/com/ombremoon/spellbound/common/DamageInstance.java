package com.ombremoon.spellbound.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.EffectManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record DamageInstance(ResourceKey<DamageType> damageType, float amount, Optional<BuildUp> additionalBuildUp, Optional<TagKey<EntityType<?>>> exclusiveTo) {
    public static final Codec<DamageInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(DamageInstance::damageType),
                    Codec.FLOAT.fieldOf("amount").forGetter(DamageInstance::amount),
                    BuildUp.CODEC.optionalFieldOf("additional_build_up").forGetter(DamageInstance::additionalBuildUp),
                    TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("exclusive_to").forGetter(DamageInstance::exclusiveTo)
            ).apply(instance, DamageInstance::new)
    );

    public static DamageInstance of(ResourceKey<DamageType> damageType, float amount, BuildUp additionalBuildUp, TagKey<EntityType<?>> exclusiveTo) {
        return new DamageInstance(damageType, amount, Optional.of(additionalBuildUp), Optional.of(exclusiveTo));
    }

    public static DamageInstance of(ResourceKey<DamageType> damageType, float amount, BuildUp additionalBuildUp) {
        return new DamageInstance(damageType, amount, Optional.of(additionalBuildUp), Optional.empty());
    }

    public static DamageInstance of(ResourceKey<DamageType> damageType, float amount, TagKey<EntityType<?>> exclusiveTo) {
        return new DamageInstance(damageType, amount, Optional.empty(), Optional.of(exclusiveTo));
    }

    public static DamageInstance of(ResourceKey<DamageType> damageType, float amount) {
        return new DamageInstance(damageType, amount, Optional.empty(), Optional.empty());
    }

    public static DamageInstance ofType(ResourceKey<DamageType> damageType) {
        return DamageInstance.of(damageType, 0.0F);
    }

    public record BuildUp(EffectManager.Effect effect, float amount) {
        public static final Codec<BuildUp> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EffectManager.Effect.CODEC.fieldOf("effet").forGetter(BuildUp::effect),
                        Codec.FLOAT.fieldOf("amount").forGetter(BuildUp::amount)
                ).apply(instance, BuildUp::new)
        );
    }
}
