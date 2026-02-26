package com.ombremoon.spellbound.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MovementData(float xxa, float zza, boolean jumping, boolean shiftKeyDown) {
    public static final Codec<MovementData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("xxa").forGetter(MovementData::xxa),
            Codec.FLOAT.fieldOf("zza").forGetter(MovementData::zza),
            Codec.BOOL.fieldOf("jumping").forGetter(MovementData::jumping),
            Codec.BOOL.fieldOf("shiftKeyDown").forGetter(MovementData::shiftKeyDown)
    ).apply(instance, MovementData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MovementData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, MovementData::xxa,
            ByteBufCodecs.FLOAT, MovementData::zza,
            ByteBufCodecs.BOOL, MovementData::jumping,
            ByteBufCodecs.BOOL, MovementData::shiftKeyDown,
            MovementData::new
    );

    public boolean isMoving() {
        return this.xxa != 0 || this.zza != 0;
    }
}
