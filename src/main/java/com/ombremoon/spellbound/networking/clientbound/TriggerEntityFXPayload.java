package com.ombremoon.spellbound.networking.clientbound;

import com.mojang.datafixers.util.Either;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TriggerEntityFXPayload(int entityId, Either<EffectData, ResourceLocation> effect) implements CustomPacketPayload {
    public static final Type<TriggerEntityFXPayload> TYPE = new Type<>(CommonClass.customLocation("trigger_entity_fx"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerEntityFXPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TriggerEntityFXPayload::entityId,
            ByteBufCodecs.either(EffectData.STREAM_CODEC, ResourceLocation.STREAM_CODEC), TriggerEntityFXPayload::effect,
            TriggerEntityFXPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
