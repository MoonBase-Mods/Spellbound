package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.Constants;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SBEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Constants.MOD_ID);

//    public static final Supplier<EntityDataSerializer<Optional<List<EffectHolder>>>> MAGIC_EFFECT = ENTITY_DATA_SERIALIZERS.register("magic_effect", () -> EntityDataSerializer.forValueType(EffectHolder.OPTIONAL_LIST_CODEC));

    public static void register(IEventBus modEventBus) {
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
    }
}
