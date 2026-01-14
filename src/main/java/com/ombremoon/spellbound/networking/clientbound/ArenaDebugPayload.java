package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ArenaDebugPayload(boolean enabled, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockPos spawnPos, BlockPos originPos) implements CustomPacketPayload {
    public static final Type<ArenaDebugPayload> TYPE = new Type<>(CommonClass.customLocation("arena_debug"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArenaDebugPayload> STREAM_CODEC = StreamCodec.ofMember(
            ArenaDebugPayload::write, ArenaDebugPayload::new
    );

    public ArenaDebugPayload(RegistryFriendlyByteBuf byteBuf) {
        this(
                byteBuf.readBoolean(),
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readBlockPos(),
                byteBuf.readBlockPos()
        );
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enabled);
        buffer.writeInt(this.minX);
        buffer.writeInt(this.minY);
        buffer.writeInt(this.minZ);
        buffer.writeInt(this.maxX);
        buffer.writeInt(this.maxY);
        buffer.writeInt(this.maxZ);
        buffer.writeBlockPos(this.spawnPos);
        buffer.writeBlockPos(this.originPos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
