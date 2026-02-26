package com.ombremoon.spellbound.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class SerializationUtil {
    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> REGISTRY_RESOURCE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ResourceLocation::getNamespace,
            ByteBufCodecs.STRING_UTF8, ResourceLocation::getPath,
            ResourceLocation::fromNamespaceAndPath
    );

    public static final Codec<Float> NON_NEGATIVE_FLOAT = floatRangeWithMessage(0.0F, Float.MAX_VALUE, p_274847_ -> "Value must be non-negative: " + p_274847_);

    private static Codec<Float> floatRangeWithMessage(float min, float max, Function<Float, String> errorMessage) {
        return Codec.FLOAT
                .validate(
                        p_274865_ -> p_274865_.compareTo(min) >= 0 && p_274865_.compareTo(max) <= 0
                                ? DataResult.success(p_274865_)
                                : DataResult.error(() -> errorMessage.apply(p_274865_))
                );
    }
}
