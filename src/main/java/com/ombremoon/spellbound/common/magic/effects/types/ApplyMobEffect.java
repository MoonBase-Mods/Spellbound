package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ApplyMobEffect(HolderSet<MobEffect> toApply, int minDuration, int maxDuration, int minAmplifier, int maxAmplifier) implements MagicEffect {


    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        RandomSource randomsource = target.getRandom();
        Optional<Holder<MobEffect>> optional = this.toApply.getRandomElement(randomsource);
        if (optional.isPresent()) {
            int i = Math.round(Mth.randomBetween(randomsource, this.minDuration, this.maxDuration) * 20.0F);
            int j = Math.max(0, Math.round(Mth.randomBetween(randomsource, this.minAmplifier, this.maxAmplifier)));
            target.addEffect(new MobEffectInstance(optional.get(), i, j));
        }
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.APPLY_MOB_EFFECT.get();
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

    public static class Serializer implements MagicEffect.Serializer<ApplyMobEffect> {
        public static final MapCodec<ApplyMobEffect> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("to_apply").forGetter(ApplyMobEffect::toApply),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_duration").forGetter(ApplyMobEffect::minDuration),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_duration").forGetter(ApplyMobEffect::maxDuration),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_amplifier").forGetter(ApplyMobEffect::minAmplifier),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_amplifier").forGetter(ApplyMobEffect::maxAmplifier)
                ).apply(instance, ApplyMobEffect::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ApplyMobEffect> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<ApplyMobEffect> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplyMobEffect> streamCodec() {
            return STREAM_CODEC;
        }
    }
}


