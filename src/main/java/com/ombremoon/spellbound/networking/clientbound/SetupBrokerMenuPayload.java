package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.Optional;

public record SetupBrokerMenuPayload(int containerId, int merchantId) implements CustomPacketPayload {
    public static final Type<SetupBrokerMenuPayload> TYPE = new Type<>(CommonClass.customLocation("setup_broker_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetupBrokerMenuPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetupBrokerMenuPayload::containerId,
            ByteBufCodecs.VAR_INT, SetupBrokerMenuPayload::merchantId,
            SetupBrokerMenuPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
