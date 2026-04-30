package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record DynamicLevelSpawnData(Vec3 playerOffset, float playerRotation, Vec3 spellOffset, Optional<ResourceLocation> spellFX) {
    public static final DynamicLevelSpawnData DEFAULT = new DynamicLevelSpawnData(Vec3.ZERO, 0f, Vec3.ZERO);
    public static final Codec<DynamicLevelSpawnData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Vec3.CODEC.fieldOf("player_offset").forGetter(DynamicLevelSpawnData::playerOffset),
                    Codec.FLOAT.fieldOf("player_rotation").forGetter(DynamicLevelSpawnData::playerRotation),
                    Vec3.CODEC.fieldOf("spell_offset").forGetter(DynamicLevelSpawnData::spellOffset),
                    ResourceLocation.CODEC.optionalFieldOf("spell_fx").forGetter(DynamicLevelSpawnData::spellFX)
            ).apply(instance, DynamicLevelSpawnData::new)
    );

   public DynamicLevelSpawnData(Vec3 playerOffset, float playerRotation, Vec3 spellOffset) {
        this(playerOffset, playerRotation, spellOffset, Optional.empty());
   }

    public static class Builder {
       private Vec3 playerOffset = Vec3.ZERO;
       private float playerRotation = 0f;
       private Vec3 spellOffset = Vec3.ZERO;
       private Optional<ResourceLocation> spellFX = Optional.empty();

       public static Builder create() {
           return new Builder();
       }

       public Builder playerOffset(Vec3 offset) {
           this.playerOffset = offset;
           return this;
       }

       public Builder playerRotation(float rotation) {
           this.playerRotation = rotation;
           return this;
       }

       public Builder spellOffset(Vec3 offset) {
           this.spellOffset = offset;
           return this;
       }

       public Builder spellFX(ResourceLocation fx) {
           this.spellFX = Optional.of(fx);
           return this;
       }

       public DynamicLevelSpawnData build() {
           return new DynamicLevelSpawnData(this.playerOffset, this.playerRotation, this.spellOffset, this.spellFX);
       }
   }
}