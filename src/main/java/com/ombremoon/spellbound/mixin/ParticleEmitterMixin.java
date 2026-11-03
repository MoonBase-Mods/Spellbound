package com.ombremoon.spellbound.mixin;

import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.ombremoon.spellbound.client.photon.FXObjectFactory;
import com.ombremoon.spellbound.client.photon.PhotonHelper;
import com.ombremoon.spellbound.client.photon.converter.ConfigBuilder;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParticleEmitter.class)
public class ParticleEmitterMixin implements FXObjectFactory<ParticleEmitter, ParticleConfig> {

    @Shadow
    @Final
    public ParticleConfig config;

    @Override
    public ParticleEmitter createEmitter(HolderLookup.Provider registries, ConfigBuilder<ParticleConfig> config) {
        return PhotonHelper.createParticle(
                this.config,
                config,
                registries);
    }
}
