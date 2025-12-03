package com.ombremoon.spellbound.common.world.multiblock;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MultiblockHolder<T extends Multiblock>(ResourceLocation id, T value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiblockHolder<?>> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MultiblockHolder::id,
            Multiblock.STREAM_CODEC, MultiblockHolder::value,
            MultiblockHolder::new
    );

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof MultiblockHolder<?> multiblockHolder && this.id.equals(multiblockHolder.id);
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
