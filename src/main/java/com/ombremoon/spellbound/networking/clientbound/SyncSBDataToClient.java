package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.main.CommonClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public record SendGuideBooksPayload(Map<ResourceLocation, List<GuideBookPage>> pages, Map<ResourceLocation, Integer> pageIndex, Map<ResourceLocation, TransfigurationRitual> rituals) implements CustomPacketPayload {
    public static final Type<SendGuideBooksPayload> TYPE = new Type<>(CommonClass.customLocation("send_guide_books_payload"));

    public static final StreamCodec<ByteBuf, SendGuideBooksPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.fromCodec(GuideBookPage.CODEC).apply(ByteBufCodecs.list())), SendGuideBooksPayload::pages,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT), SendGuideBooksPayload::pageIndex,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.fromCodec(TransfigurationRitual.DIRECT_CODEC)), SendGuideBooksPayload::rituals,
            SendGuideBooksPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
