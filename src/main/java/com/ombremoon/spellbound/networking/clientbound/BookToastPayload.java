package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.client.gui.guide.renderers.init.GuideBlockAndTintGetter;
import com.ombremoon.spellbound.client.gui.toasts.SpellboundToasts;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import com.ombremoon.spellbound.util.StructureInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BookToastPayload(int toastOrdinal) implements CustomPacketPayload {
    public static final Type<BookToastPayload> TYPE = new Type<>(CommonClass.customLocation("book_toast"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookToastPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BookToastPayload::toastOrdinal,
            BookToastPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BookToastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            RenderUtil.sendBookToast(SpellboundToasts.values()[payload.toastOrdinal()]);
        });
    }
}
