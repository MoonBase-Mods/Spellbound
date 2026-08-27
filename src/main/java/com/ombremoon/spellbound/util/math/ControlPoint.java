package com.ombremoon.spellbound.util.math;

import com.ombremoon.spellbound.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record ControlPoint(double peakParameter, Vec3 gravityVector) {
    public static final StreamCodec<ByteBuf, ControlPoint> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ControlPoint::peakParameter,
            SerializationUtil.VEC3_STREAM_CODEC, ControlPoint::gravityVector,
            ControlPoint::new
    );
}
