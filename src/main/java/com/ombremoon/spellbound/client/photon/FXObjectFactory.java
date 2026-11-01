package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.lowdraglib2.configurator.IConfigurable;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.ombremoon.spellbound.client.photon.converter.ConfigBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.Level;

public interface FXObjectFactory<T extends IFXObject, S extends IConfigurable> {

    T createEmitter(HolderLookup.Provider registries, ConfigBuilder<S> config);
}
