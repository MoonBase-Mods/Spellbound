package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.common.world.multiblock.MultiblockHolder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record UpdateMultiblocksPayload(List<MultiblockHolder<?>> multiblocks) implements CustomPacketPayload {
    public static final Type<UpdateMultiblocksPayload> TYPE = new Type<>(CommonClass.customLocation("update_multiblocks"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateMultiblocksPayload> STREAM_CODEC = StreamCodec.composite(
            MultiblockHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), payload -> payload.multiblocks,
            UpdateMultiblocksPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
