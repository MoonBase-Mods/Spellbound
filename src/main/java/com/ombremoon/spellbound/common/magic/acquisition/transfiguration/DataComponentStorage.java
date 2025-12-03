package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record DataComponentStorage(List<TypedDataComponent<?>> dataComponents) {
    public static Codec<DataComponentStorage> CODEC = DataComponentType.VALUE_MAP_CODEC
            .xmap(map -> new DataComponentStorage(map.entrySet().stream().map(DataComponentStorage::fromEntryUnchecked).collect(Collectors.toList())),
                    storage -> storage.dataComponents
                            .stream()
                            .filter(typedDataComponent -> !typedDataComponent.type().isTransient())
                            .collect(Collectors.toMap(TypedDataComponent::type, TypedDataComponent::value))
            );

    static TypedDataComponent<?> fromEntryUnchecked(Map.Entry<DataComponentType<?>, Object> entry) {
        return createUnchecked(entry.getKey(), entry.getValue());
    }

    public static <T> TypedDataComponent<T> createUnchecked(DataComponentType<T> type, Object value) {
        return new TypedDataComponent<>(type, (T)value);
    }

    public static Optional<DataComponentStorage> optionalOf(TypedDataComponent<?>... data) {
        return Optional.of(of(data));
    }

    public static DataComponentStorage of(TypedDataComponent<?>... data) {
        return new DataComponentStorage(Arrays.asList(data));
    }
}
