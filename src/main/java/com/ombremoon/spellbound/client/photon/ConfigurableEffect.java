package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.lowdraglib2.configurator.IConfigurable;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXData;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.ombremoon.spellbound.client.photon.converter.ConfigBuilder;
import net.minecraft.core.HolderLookup;

import java.util.ArrayList;
import java.util.List;

public interface ConfigurableEffect {

    List<ConfigBuilder<?>> modifiedConfigs();

    void addModifiedConfig(ConfigBuilder<?> modifiedConfig);

    default FXRuntime modifyFXRuntime(FX fx, HolderLookup.Provider registries) {
        if (this.modifiedConfigs().isEmpty()) {
            FXRuntime runtime = fx.createRuntime();
            var object = runtime.findObject("particle_emitter");
            if (object instanceof ParticleEmitter emitter) {
//                Constants.LOG.info("{}", emitter.config.serializeNBT(registries));
            }
//            Constants.LOG.info("{}", runtime.fxData.objects().stream().map(IFXObject::getName).toList());
            return runtime;
//            return fx.createRuntime();
        }

        FXData data = fx.getFxData().copy(false);
        var list = new ArrayList<>(data.objects());
        list.replaceAll(object -> {
            for (var config : this.modifiedConfigs()) {
                if (object.getName().equals(config.getEmitterName()) && object instanceof FXObjectFactory<?, ?> factory) {
                    FXObjectFactory<IFXObject, IConfigurable> typedFactory = (FXObjectFactory<IFXObject, IConfigurable>) factory;
                    ConfigBuilder<IConfigurable> typedConfig = (ConfigBuilder<IConfigurable>) config;
                    return typedFactory.createEmitter(registries, typedConfig);
                }
            }
            return object;
        });

        FXData fxData = new FXData(list);
        FXRuntime runtime = new FXRuntime(fxData);
        var object = runtime.findObject("particle_emitter");
        if (object instanceof ParticleEmitter emitter) {
//            Constants.LOG.info("{}", emitter.config.serializeNBT(registries));
        }
        return runtime;
//        return new FXRuntime(fxData);
    }
}
