package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.main.CommonClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncEffectsPayload(CompoundTag tag) implements CustomPacketPayload {
    public static final Type<SyncEffectsPayload> TYPE =
            new Type<>(CommonClass.customLocation("client_effects_sync"));

    public static final StreamCodec<ByteBuf, SyncEffectsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncEffectsPayload::tag,
            SyncEffectsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
