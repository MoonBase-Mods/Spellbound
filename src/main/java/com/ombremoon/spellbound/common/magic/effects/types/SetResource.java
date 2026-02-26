package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.world.EntityResource;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SetResource(EntityResource resource, double amount, EntityResource.Operation operation) implements MagicEffect {
    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        this.resource.consume(target, this.amount, this.operation);
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.SET_RESOURCE.get();
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

    public static class Serializer implements MagicEffect.Serializer<SetResource> {
        public static final MapCodec<SetResource> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        EntityResource.CODEC.fieldOf("resource").forGetter(SetResource::resource),
                        Codec.DOUBLE.fieldOf("amount").forGetter(SetResource::amount),
                        EntityResource.Operation.CODEC.fieldOf("operation").forGetter(SetResource::operation)
                ).apply(instance, SetResource::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SetResource> STREAM_CODEC = StreamCodec.composite(
                NeoForgeStreamCodecs.enumCodec(EntityResource.class), SetResource::resource,
                ByteBufCodecs.DOUBLE, SetResource::amount,
                NeoForgeStreamCodecs.enumCodec(EntityResource.Operation.class), SetResource::operation,
                SetResource::new
        );

        @Override
        public MapCodec<SetResource> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SetResource> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
