package com.ombremoon.spellbound.networking.serverbound;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SelectRiddleTradePayload(int item) implements CustomPacketPayload {
    public static final Type<SelectRiddleTradePayload> TYPE = new Type<>(CommonClass.customLocation("select_riddle_trade"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectRiddleTradePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SelectRiddleTradePayload::item,
            SelectRiddleTradePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
