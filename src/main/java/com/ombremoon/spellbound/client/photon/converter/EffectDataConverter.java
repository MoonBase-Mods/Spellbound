package com.ombremoon.spellbound.client.photon.converter;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.type.BlockEffectType;
import com.ombremoon.spellbound.client.photon.type.EntityDeathEffectType;
import com.ombremoon.spellbound.client.photon.type.EntityEffectType;
import com.ombremoon.spellbound.client.photon.type.StaticEntityEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EffectDataConverter {
    private static final Map<EffectType<?>, Function<EffectData, EffectBuilder<?>>> CONVERTERS = new HashMap<>();

    public static void initConverters() {
        EffectDataConverter.register(EffectTypes.BLOCK, BlockEffectType::convertBlock);
        EffectDataConverter.register(EffectTypes.ENTITY, EntityEffectType::convertEntity);
        EffectDataConverter.register(EffectTypes.ENTITY_DEATH, EntityDeathEffectType::convertEntityDeath);
        EffectDataConverter.register(EffectTypes.STATIC_ENTITY, StaticEntityEffectType::convertStaticEntity);
        //TODO: ADD EVENT
    }

    /**
     * Register a converter for a specific effect type.
     * Call this during client setup to register converters for custom effect types.
     *
     * @param type The effect type to convert
     * @param converter Function that converts SpellEffectData to EffectBuilder
     */
    private static void register(EffectType<?> type, Function<EffectData, EffectBuilder<?>> converter) {
        CONVERTERS.put(type, converter);
    }

    /**
     * Convert SpellEffectData to EffectBuilder using registered converter.
     * Returns null if no converter is registered for the effect type.
     *
     * @param effectData The server-safe effect data
     * @return The client-side EffectBuilder, or null if no converter found
     */
    private static EffectBuilder<?> convertToBuilderInternal(EffectData effectData) {
        Function<EffectData, EffectBuilder<?>> converter = CONVERTERS.get(effectData.getType());
        return converter != null ? converter.apply(effectData) : null;
    }

    public static EffectBuilder<?> convertToBuilder(EffectData effectData) {
        EffectBuilder<?> builder = convertToBuilderInternal(effectData);
        if (builder != null) {
            return builder;
        } else {
            throw new IllegalStateException("No converter found for effect type: " + effectData.getType());
        }
    }
}
