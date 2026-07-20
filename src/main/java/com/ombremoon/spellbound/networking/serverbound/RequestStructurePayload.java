package com.ombremoon.spellbound.networking.serverbound;

import com.mojang.logging.LogUtils;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.StructureInfo;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RequestStructurePayload(ResourceLocation structure) implements CustomPacketPayload {
    public static final Type<RequestStructurePayload> TYPE = new Type<>(CommonClass.customLocation("request_structure"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestStructurePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RequestStructurePayload::structure,
            RequestStructurePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestStructurePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.player().level();
            ResourceLocation structure = payload.structure();
            StructureInfo info;

            Optional<StructureTemplate> optionalTemplate = level.getStructureManager().get(structure);
            if (optionalTemplate.isEmpty()) {
                LogUtils.getLogger().debug("Template: {}, Could not be found.", structure);
                info = new StructureInfo(structure, new Vector3f(0, 0, 0), List.of());
            } else {
                StructureTemplate template = optionalTemplate.get();
                if (template.palettes.isEmpty()) {
                    LogUtils.getLogger().debug("Template {}, had no palettes.", structure);
                    info = new StructureInfo(structure, new Vector3f(0, 0, 0), List.of());
                } else info = new StructureInfo(
                        structure,
                        fromVec3i(template.getSize()),
                        StructureInfo.BlockData.structureToBlockData(template.palettes.getFirst().blocks()));
            }

            PayloadHandler.sendStructureDataToClient((ServerPlayer) context.player(), info);
        });
    }

    private static Vector3f fromVec3i(Vec3i vec) {
        return new Vector3f(
                (float) vec.getX(), (float) vec.getY(), (float) vec.getZ()
        );
    }
}
