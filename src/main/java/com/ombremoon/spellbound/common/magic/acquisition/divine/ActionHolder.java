package com.ombremoon.spellbound.common.magic.acquisition.divine;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ActionHolder(ResourceLocation id, SpellAction value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ActionHolder> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ActionHolder::id,
            SpellAction.STREAM_CODEC, ActionHolder::value,
            ActionHolder::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ActionHolder>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof ActionHolder actionHolder && this.id.equals(actionHolder.id);
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
