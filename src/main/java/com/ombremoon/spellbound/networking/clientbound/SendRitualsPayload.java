package com.ombremoon.spellbound.networking.clientbound;

import com.google.common.collect.ImmutableMap;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SendRitualsPayload(Map<ResourceLocation, NonNullList<TransfigurationRitual.Value>> rituals) implements CustomPacketPayload {
    public static final Type<SendRitualsPayload> TYPE = new Type<>(CommonClass.customLocation("send_rituals_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SendRitualsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.fromCodec(NonNullList.codecOf(TransfigurationRitual.Value.CODEC))), SendRitualsPayload::rituals,
            SendRitualsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendRitualsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            RitualHelper.setClientRituals(payload.rituals());
        });
    }
}
