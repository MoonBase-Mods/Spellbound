package com.ombremoon.spellbound.common.magic.api.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AnimProperties(int startTick, int anticipationTick, int preDelayTick, int attackTick, int recoveryTick) {
    public static final Codec<AnimProperties> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("startTick").forGetter(AnimProperties::startTick),
                    Codec.INT.fieldOf("anticipationTick").forGetter(AnimProperties::anticipationTick),
                    Codec.INT.fieldOf("preDelayTick").forGetter(AnimProperties::preDelayTick),
                    Codec.INT.fieldOf("attackTick").forGetter(AnimProperties::attackTick),
                    Codec.INT.fieldOf("recoveryTick").forGetter(AnimProperties::recoveryTick)
            ).apply(instance, AnimProperties::new)
    );
    public static final StreamCodec<ByteBuf, AnimProperties> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AnimProperties::startTick,
            ByteBufCodecs.VAR_INT, AnimProperties::anticipationTick,
            ByteBufCodecs.VAR_INT, AnimProperties::preDelayTick,
            ByteBufCodecs.VAR_INT, AnimProperties::attackTick,
            ByteBufCodecs.VAR_INT, AnimProperties::recoveryTick,
            AnimProperties::new
    );
    public static final AnimProperties EMPTY = new AnimProperties(0, 0, 0, 0, 0);
}
