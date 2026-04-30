package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public record DungeonRule<T>(RuleType<T> rule, List<T> exception) {
    public static final Codec<DungeonRule<?>> CODEC = RuleType.MAP_CODEC
            .xmap(map -> DungeonRule.fromEntryUnchecked(map.entrySet().iterator().next()),
                    rule -> Map.of(rule.rule(), rule.exception())
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, DungeonRule<?>> STREAM_CODEC = new StreamCodec<>() {
        public DungeonRule<?> decode(RegistryFriendlyByteBuf buffer) {
            RuleType<?> rule = RuleType.STREAM_CODEC.decode(buffer);
            return decodeTyped(buffer, rule);
        }

        private static <T> DungeonRule<T> decodeTyped(RegistryFriendlyByteBuf buffer, RuleType<T> rule) {
            return new DungeonRule<>(rule, rule.streamCodec().apply(ByteBufCodecs.list()).decode(buffer));
        }

        public void encode(RegistryFriendlyByteBuf buffer, DungeonRule<?> value) {
            encodeCap(buffer, value);
        }

        private static <T> void encodeCap(RegistryFriendlyByteBuf buffer, DungeonRule<T> rule) {
            RuleType.STREAM_CODEC.encode(buffer, rule.rule());
            rule.rule().streamCodec().apply(ByteBufCodecs.list()).encode(buffer, rule.exception());
        }
    };

    static DungeonRule<?> fromEntryUnchecked(Map.Entry<RuleType<?>, List<?>> entry) {
        return createUnchecked(entry.getKey(), (List<Object>) entry.getValue());
    }

    public static <T> DungeonRule<T> createUnchecked(RuleType<T> type, List<Object> value) {
        return new DungeonRule<>(type, (List<T>) value);
    }

    public boolean test(T value) {
        return this.exception.contains(value);
    }

    public <D> DataResult<D> encodeValue(DynamicOps<D> ops) {
        Codec<List<T>> codec = this.rule.codec().listOf();
        return codec == null ? DataResult.error(() -> "Component of type " + this.rule + " is not encodable") : codec.encodeStart(ops, this.exception);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DungeonRule<?> other && this.rule.equals(other.rule) && this.exception.equals(other.exception);
    }
}
