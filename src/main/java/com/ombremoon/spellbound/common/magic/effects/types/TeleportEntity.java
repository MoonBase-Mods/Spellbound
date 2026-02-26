package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.magic.effects.RangeProvider;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.util.RandomPosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record TeleportEntity(
        RangeProvider range,
        boolean allowAirOrWater
) implements MagicEffect {
    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        RandomSource randomsource = target.getRandom();
        int i = (int) this.range.getRadius(randomsource);
        int j = (int) this.range.getHeight(randomsource);
        Vec3 pos;
        if (this.allowAirOrWater) {
            pos = RandomPosUtil.getAirAndWaterPos(target, i, j, 0, RandomUtil.randomValueBetween(-1.0, 1.0), RandomUtil.randomValueBetween(-1.0, 1.0), Mth.TWO_PI);
        } else {
            pos = RandomPosUtil.getLandPos(target, i, j);
        }

        if (pos != null) {
            target.teleportTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
        }
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.TELEPORT_ENTITY.get();
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

    public static class Serializer implements MagicEffect.Serializer<TeleportEntity> {
        public static final MapCodec<TeleportEntity> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        RangeProvider.CODEC.fieldOf("range").forGetter(TeleportEntity::range),
                        Codec.BOOL.optionalFieldOf("allow_air_or_water", false).forGetter(TeleportEntity::allowAirOrWater)
                ).apply(instance, TeleportEntity::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, TeleportEntity> STREAM_CODEC = StreamCodec.composite(
                RangeProvider.STREAM_CODEC, TeleportEntity::range,
                ByteBufCodecs.BOOL, TeleportEntity::allowAirOrWater,
                TeleportEntity::new
        );

        @Override
        public MapCodec<TeleportEntity> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TeleportEntity> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
