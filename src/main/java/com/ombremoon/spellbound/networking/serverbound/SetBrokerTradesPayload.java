package com.ombremoon.spellbound.networking.serverbound;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SetBrokerTradesPayload(int merchantId, int containerId, boolean isRiddle) implements CustomPacketPayload {
    public static final Type<SetBrokerTradesPayload> TYPE = new Type<>(CommonClass.customLocation("set_broker_trades"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetBrokerTradesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetBrokerTradesPayload::merchantId,
            ByteBufCodecs.VAR_INT, SetBrokerTradesPayload::containerId,
            ByteBufCodecs.BOOL, SetBrokerTradesPayload::isRiddle,
            SetBrokerTradesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
