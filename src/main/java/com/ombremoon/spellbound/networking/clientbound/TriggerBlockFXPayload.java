package com.ombremoon.spellbound.networking.clientbound;

import com.mojang.datafixers.util.Either;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TriggerBlockFXPayload(BlockPos blockPos, Either<EffectData, ResourceLocation> effect) implements CustomPacketPayload {
    public static final Type<TriggerBlockFXPayload> TYPE = new Type<>(CommonClass.customLocation("trigger_block_fx"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerBlockFXPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TriggerBlockFXPayload::blockPos,
            ByteBufCodecs.either(EffectData.STREAM_CODEC, ResourceLocation.STREAM_CODEC), TriggerBlockFXPayload::effect,
            TriggerBlockFXPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
