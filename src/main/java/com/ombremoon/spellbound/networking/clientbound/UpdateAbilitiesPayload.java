package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.main.CommonClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateAbilitiesPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateAbilitiesPayload> TYPE =
            new CustomPacketPayload.Type<>(CommonClass.customLocation("update_abilities"));

    public static final StreamCodec<ByteBuf, UpdateAbilitiesPayload> STREAM_CODEC = StreamCodec.unit(new UpdateAbilitiesPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
