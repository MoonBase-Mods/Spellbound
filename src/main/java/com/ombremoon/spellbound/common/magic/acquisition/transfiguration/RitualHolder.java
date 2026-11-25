package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RitualHolder(ResourceLocation id, TransfigurationRitual ritual) {
    public static final StreamCodec<RegistryFriendlyByteBuf, RitualHolder> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RitualHolder::id,
            TransfigurationRitual.DIRECT_STREAM_CODEC, RitualHolder::ritual,
            RitualHolder::new
    );
}
