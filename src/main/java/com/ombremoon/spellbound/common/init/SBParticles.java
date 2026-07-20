package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Constants.MOD_ID);

    public static final Supplier<SimpleParticleType> SLUDGE = PARTICLES.register("sludge", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> MUSHROOM_SPORE = PARTICLES.register("mushroom_spore", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> GOLD_HEART = PARTICLES.register("gold_heart", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> TEST = PARTICLES.register("test", () -> new SimpleParticleType(true));

    public static void register(IEventBus modEventBus) {
        PARTICLES.register(modEventBus);
    }
}
