package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.main.CommonClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncSpellHandlerPayload(CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncSpellHandlerPayload> TYPE =
            new CustomPacketPayload.Type<>(CommonClass.customLocation("client_spell_sync"));

    public static final StreamCodec<ByteBuf, SyncSpellHandlerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncSpellHandlerPayload::tag,
            SyncSpellHandlerPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
