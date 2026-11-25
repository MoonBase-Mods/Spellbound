package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHolder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record UpdateRitualsPayload(List<RitualHolder> rituals) implements CustomPacketPayload {
    public static final Type<UpdateRitualsPayload> TYPE = new Type<>(CommonClass.customLocation("update_rituals"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRitualsPayload> STREAM_CODEC = StreamCodec.composite(
            RitualHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), UpdateRitualsPayload::rituals,
            UpdateRitualsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
