package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SummonEntity(HolderSet<EntityType<?>> entityTypes, boolean joinTeam) implements MagicEffect {
    public static final MapCodec<SummonEntity> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(SummonEntity::entityTypes),
                    Codec.BOOL.optionalFieldOf("join_team", false).forGetter(SummonEntity::joinTeam)
            ).apply(instance, SummonEntity::new)
    );

    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        if (Level.isInSpawnableBounds(centerPos)) {
            Optional<Holder<EntityType<?>>> optional = this.entityTypes().getRandomElement(level.getRandom());
            if (optional.isPresent()) {
                Entity entity = optional.get().value().spawn(level, centerPos, MobSpawnType.TRIGGERED);
                if (entity != null) {
                    if (entity instanceof LightningBolt lightningbolt && source instanceof ServerPlayer serverplayer) {
                        lightningbolt.setCause(serverplayer);
                    }

                    if (this.joinTeam && caster.getTeam() != null) {
                        level.getScoreboard().addPlayerToTeam(entity.getScoreboardName(), caster.getTeam());
                    }

                    entity.moveTo(centerPos.getX(), centerPos.getY(), centerPos.getZ(), entity.getYRot(), entity.getXRot());
                }
            }
        }
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.SUMMON_ENTITY.get();
    }

    @Override
    public LootContext createContext(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern, @Nullable DamageSource damageSource) {
        LootParams lootparams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, caster)
                .withParameter(LootContextParams.ORIGIN, centerPos.getCenter())
                .withOptionalParameter(EffectContextParamSets.RITUAL_TIER, tier)
                .create(EffectContextParamSets.MAGIC_ENTITY);
        return new LootContext.Builder(lootparams).create(Optional.empty());
    }

    public static class Serializer implements MagicEffect.Serializer<SummonEntity> {
        public static final MapCodec<SummonEntity> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(SummonEntity::entityTypes),
                        Codec.BOOL.optionalFieldOf("join_team", false).forGetter(SummonEntity::joinTeam)
                ).apply(instance, SummonEntity::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SummonEntity> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderSet(Registries.ENTITY_TYPE), SummonEntity::entityTypes,
                ByteBufCodecs.BOOL, SummonEntity::joinTeam,
                SummonEntity::new
        );

        @Override
        public MapCodec<SummonEntity> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SummonEntity> streamCodec() {
            return STREAM_CODEC;
        }
    }
}