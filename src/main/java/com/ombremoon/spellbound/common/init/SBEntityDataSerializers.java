package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.entity.projectile.BoundArrow;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SerializationUtil;
import com.ombremoon.spellbound.util.math.SplineController;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SBEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Constants.MOD_ID);

    public static final Supplier<EntityDataSerializer<Vec3>> VEC3 = ENTITY_DATA_SERIALIZERS.register("vec3", () -> EntityDataSerializer.forValueType(SerializationUtil.VEC3_STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<SplineController>> SPLINE_CONTROLLER = ENTITY_DATA_SERIALIZERS.register("spline_controller", () -> EntityDataSerializer.forValueType(SplineController.STREAM_CODEC));
    public static final Supplier<EntityDataSerializer<BoundArrow.ArrowVariant>> ARROW_VARIANT = ENTITY_DATA_SERIALIZERS.register("arrow_variant", () -> EntityDataSerializer.forValueType(BoundArrow.ArrowVariant.STREAM_CODEC));

    public static void register(IEventBus modEventBus) {
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
    }
}
