package com.ombremoon.spellbound.client.photon.converter;

import com.ombremoon.spellbound.client.photon.type.BlockEffectType;
import com.ombremoon.spellbound.client.photon.type.EntityDeathEffectType;
import com.ombremoon.spellbound.client.photon.type.EntityEffectType;
import com.ombremoon.spellbound.client.photon.type.StaticEntityEffectType;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SerializationUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class EffectTypes {
    private static final Map<ResourceLocation, EffectType<?>> REGISTRY = new HashMap<>();
    public static final EffectType<EffectData.Block> BLOCK = new BlockEffectType();
    public static final EffectType<EffectData.Entity> ENTITY = new EntityEffectType();
    public static final EffectType<EffectData.DeadEntity> ENTITY_DEATH = new EntityDeathEffectType();
    public static final EffectType<EffectData.StaticEntity> STATIC_ENTITY = new StaticEntityEffectType();

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectType<?>> STREAM_CODEC = SerializationUtil.REGISTRY_RESOURCE_STREAM_CODEC
            .map(EffectTypes::getEffectTypeFromLocation, EffectType::id);

    public static void initEffectTypes() {
        register("block", BLOCK);
        register("entity", ENTITY);
        register("entity_death", ENTITY_DEATH);
        register("static_entity", STATIC_ENTITY);
        //TODO: ADD EVENT
    }

    public static EffectType<?> getEffectTypeFromLocation(ResourceLocation resourceLocation) {
        return REGISTRY.getOrDefault(resourceLocation, null);
    }

    private static <S extends EffectType<?>> S register(String name, S provider) {
        REGISTRY.put(CommonClass.customLocation(name), provider);
        return provider;
    }
}
