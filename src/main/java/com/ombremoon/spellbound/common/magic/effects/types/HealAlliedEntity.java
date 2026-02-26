package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.magic.effects.RangeProvider;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record HealAlliedEntity(RangeProvider range, float minAmount, float maxAmount) implements MagicEffect {
    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        RandomSource randomsource = target.getRandom();
        int i = Math.round(Mth.randomBetween(randomsource, this.minAmount, this.maxAmount));
        int j = Math.round(Mth.randomBetween(randomsource, this.range.minRadius(), this.range.maxRadius()));
        int k = Math.round(Mth.randomBetween(randomsource, this.range.minHeight(), this.range.maxHeight()));
        var list = level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(j, k, j), entity -> SpellUtil.IS_ALLIED.test(entity, target));
        for (LivingEntity entity : list) {
            entity.heal(i);
        }
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.HEAL_ENTITY.get();
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

    public static class Serializer implements MagicEffect.Serializer<HealAlliedEntity> {
        public static final MapCodec<HealAlliedEntity> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        RangeProvider.CODEC.fieldOf("range").forGetter(HealAlliedEntity::range),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("minAmount").forGetter(HealAlliedEntity::minAmount),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("maxAmount").forGetter(HealAlliedEntity::maxAmount)
                ).apply(instance, HealAlliedEntity::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, HealAlliedEntity> STREAM_CODEC = StreamCodec.composite(
                RangeProvider.STREAM_CODEC, HealAlliedEntity::range,
                ByteBufCodecs.FLOAT, HealAlliedEntity::minAmount,
                ByteBufCodecs.FLOAT, HealAlliedEntity::maxAmount,
                HealAlliedEntity::new
        );

        @Override
        public MapCodec<HealAlliedEntity> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HealAlliedEntity> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
