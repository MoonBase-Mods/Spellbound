package com.ombremoon.spellbound.client.photon.converter;

import com.lowdragmc.lowdraglib2.configurator.IConfigurable;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.ombremoon.spellbound.client.photon.data.EmissionBuilder;
import com.ombremoon.spellbound.client.photon.data.NumberFunctionBuilder;
import com.ombremoon.spellbound.client.photon.data.ParticleRendererBuilder;
import com.ombremoon.spellbound.client.photon.data.ShapeSettingBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public abstract class ConfigBuilder<T extends IConfigurable> {
    protected final String emitterName;
    protected final HolderLookup.Provider registries;
    protected final CompoundTag config = new CompoundTag();

    protected ConfigBuilder(String emitterName, HolderLookup.Provider registries) {
        this.emitterName = emitterName;
        this.registries = registries;
    }

    public String getEmitterName() {
        return this.emitterName;
    }

    public abstract T createConfig(HolderLookup.Provider registries, CompoundTag config);

    public static class Particle extends ConfigBuilder<ParticleConfig> {

        protected Particle(String emitterName, HolderLookup.Provider registries) {
            super(emitterName, registries);
        }

        public static Particle of(String emitterName, HolderLookup.Provider registries) {
            return new Particle(emitterName, registries);
        }

        public Particle duration(int duration) {
            this.config.putInt("duration", duration);
            return this;
        }

        public Particle looping(boolean looping) {
            this.config.putBoolean("looping", looping);
            return this;
        }

        public Particle prewarm(int prewarm) {
            this.config.putInt("prewarm", prewarm);
            return this;
        }

        public Particle startDelay(NumberFunctionBuilder startDelay) {
            this.config.put("startDelay", startDelay.build());
            return this;
        }

        public Particle startLifetime(NumberFunctionBuilder startLifetime) {
            this.config.put("startLifetime", startLifetime.build());
            return this;
        }

        public Particle startSpeed(NumberFunctionBuilder startSpeed) {
            this.config.put("startSpeed", startSpeed.build());
            return this;
        }

        public Particle startSize(NumberFunctionBuilder x, NumberFunctionBuilder y, NumberFunctionBuilder z) {
            ListTag listTag = new ListTag();
            listTag.add(x.build());
            listTag.add(y.build());
            listTag.add(z.build());
            this.config.put("startSize", listTag);
            return this;
        }

        public Particle startSize(NumberFunctionBuilder function) {
            return this.startSize(function, function, function);
        }

        public Particle startRotation(NumberFunctionBuilder x, NumberFunctionBuilder y, NumberFunctionBuilder z) {
            ListTag listTag = new ListTag();
            listTag.add(x.build());
            listTag.add(y.build());
            listTag.add(z.build());
            this.config.put("startRotation", listTag);
            return this;
        }

        public Particle startRotation(NumberFunctionBuilder function) {
            return this.startRotation(function, function, function);
        }

        public Particle startColor(NumberFunctionBuilder startColor) {
            this.config.put("startColor", startColor.build());
            return this;
        }

        public Particle space(ParticleConfig.Space space) {
            this.config.putString("space", space.name());
            return this;
        }

        public Particle maxParticles(int maxParticles) {
            this.config.putInt("maxParticles", maxParticles);
            return this;
        }

        public Particle parallelUpdate(boolean parallelUpdate) {
            this.config.putBoolean("parallelUpdate", parallelUpdate);
            return this;
        }

        public Particle emission(EmissionBuilder emission) {
            this.config.put("emission", emission.build());
            return this;
        }

        public Particle shape(ShapeSettingBuilder shape) {
            this.config.put("shape", shape.build());
            return this;
        }

        public Particle renderer(ParticleRendererBuilder renderer) {
            this.config.put("renderer", renderer.build());
            return this;
        }

        @Override
        public ParticleConfig createConfig(HolderLookup.Provider registries, CompoundTag oldConfig) {
            ParticleConfig newConfig = new ParticleConfig();
            oldConfig.merge(this.config);
            newConfig.deserializeNBT(registries, oldConfig);
            return newConfig;
        }
    }
}
