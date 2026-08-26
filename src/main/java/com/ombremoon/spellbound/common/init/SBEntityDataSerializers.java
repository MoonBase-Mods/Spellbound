package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.entity.projectile.BoundArrow;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SBEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Constants.MOD_ID);

    public static final Supplier<EntityDataSerializer<BoundArrow.ArrowVariant>> ARROW_VARIANT = ENTITY_DATA_SERIALIZERS.register("arrow_variant", () -> EntityDataSerializer.forValueType(BoundArrow.ArrowVariant.STREAM_CODEC));

    public static void register(IEventBus modEventBus) {
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
    }
}
