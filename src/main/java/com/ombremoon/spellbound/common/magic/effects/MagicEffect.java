package com.ombremoon.spellbound.common.magic.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;

public interface MagicEffect {
    Codec<MagicEffect> CODEC = SBMagicEffects.REGISTRY
            .byNameCodec()
            .dispatch(MagicEffect::serializer, Serializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, MagicEffect> STREAM_CODEC = ByteBufCodecs.registry(SBMagicEffects.RITUAL_EFFECT_REGISTRY_KEY)
            .dispatch(MagicEffect::serializer, Serializer::streamCodec);

    void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern);

    default void onDeactivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {}

    default void onPreDamage(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern, DamageContainer container) {}

    default void onPreAttack(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern, DamageContainer container) {}

    default void onPostAttack(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern, DamageSource damageSource) {}

    Serializer<? extends MagicEffect> serializer();

    LootContext createContext(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern, @Nullable DamageSource damageSource);

    interface Serializer<T extends MagicEffect> {
        MapCodec<T> codec();

        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
    }
}
