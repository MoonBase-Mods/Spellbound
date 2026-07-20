package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.client.gui.guide.renderers.init.GuideBlockAndTintGetter;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.StructureInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ImportStructurePayload(StructureInfo structureInfo) implements CustomPacketPayload {
    public static final Type<ImportStructurePayload> TYPE = new Type<>(CommonClass.customLocation("import_structure"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ImportStructurePayload> STREAM_CODEC = StreamCodec.composite(
            StructureInfo.STREAM_CODEC, ImportStructurePayload::structureInfo,
            ImportStructurePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ImportStructurePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            GuideBlockAndTintGetter.loadStructure(payload.structureInfo);
        });
    }
}
