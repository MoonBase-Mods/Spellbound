package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.entity.behavior.sensor.HurtOwnerSensor;
import com.ombremoon.spellbound.common.world.entity.behavior.sensor.NearbyShrineSensor;
import com.ombremoon.spellbound.common.world.entity.behavior.sensor.OwnerAttackSenor;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.function.Supplier;

public class SBSensors {
    public static final DeferredRegister<SensorType<?>> SENSORS = DeferredRegister.create(Registries.SENSOR_TYPE, Constants.MOD_ID);

    public static final Supplier<SensorType<HurtOwnerSensor<?>>> HURT_OWNER = register("hurt_owner", HurtOwnerSensor::new);
    public static final Supplier<SensorType<OwnerAttackSenor<?>>> OWNER_ATTACK = register("owner_attack", OwnerAttackSenor::new);
    public static final Supplier<SensorType<NearbyShrineSensor<?>>> NEARBY_SHRINE = register("nearby_shrine", NearbyShrineSensor::new);

    private static <T extends ExtendedSensor<?>> Supplier<SensorType<T>> register(String id, Supplier<T> sensor) {
        return SENSORS.register(id, () -> new SensorType<>(sensor));
    }

    public static void register(IEventBus modEventBus) {
        SENSORS.register(modEventBus);
    }
}
