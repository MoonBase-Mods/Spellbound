package com.ombremoon.spellbound.common.magic.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.util.SerializationUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public record RangeProvider(float minRadius, float maxRadius, float minHeight, float maxHeight, boolean affectsEntities) {
    public static final Codec<RangeProvider> CODEC = RecordCodecBuilder.<RangeProvider>create(
            inst -> inst.group(
                    SerializationUtil.NON_NEGATIVE_FLOAT.fieldOf("min_radius").forGetter(RangeProvider::minRadius),
                    SerializationUtil.NON_NEGATIVE_FLOAT.fieldOf("max_radius").forGetter(RangeProvider::maxRadius),
                    SerializationUtil.NON_NEGATIVE_FLOAT.fieldOf("min_height").forGetter(RangeProvider::minHeight),
                    SerializationUtil.NON_NEGATIVE_FLOAT.fieldOf("max_height").forGetter(RangeProvider::maxHeight),
                    Codec.BOOL.optionalFieldOf("affects_entities", false).forGetter(RangeProvider::affectsEntities)
            ).apply(inst, RangeProvider::new)
    ).validate(RangeProvider::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, RangeProvider> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, RangeProvider::minRadius,
            ByteBufCodecs.FLOAT, RangeProvider::maxRadius,
            ByteBufCodecs.FLOAT, RangeProvider::minHeight,
            ByteBufCodecs.FLOAT, RangeProvider::maxHeight,
            ByteBufCodecs.BOOL, RangeProvider::affectsEntities,
            RangeProvider::new
    );

    public static DataResult<RangeProvider> validate(RangeProvider provider) {
        if (provider.minRadius() > provider.maxRadius())
            return DataResult.error(() -> "minRadius must be less than or equal to maxRadius");
        if (provider.minHeight() > provider.maxHeight())
            return DataResult.error(() -> "minHeight must be less than or equal to maxHeight");
        return DataResult.success(provider);
    }

    public RangeProvider(float minRadius, float maxRadius, float minHeight, float maxHeight) {
        this(minRadius, maxRadius, minHeight, maxHeight, false);
    }

    public RangeProvider(float minRadius, float maxRadius) {
        this(minRadius, maxRadius, minRadius, maxRadius, false);
    }

    public RangeProvider(float radius) {
        this(radius, radius, radius, radius, false);
    }

    public float getRadius(RandomSource random) {
        return Mth.randomBetween(random, minRadius(), maxRadius());
    }

    public float getHeight(RandomSource random) {
        return Mth.randomBetween(random, minHeight(), maxHeight());
    }
}
