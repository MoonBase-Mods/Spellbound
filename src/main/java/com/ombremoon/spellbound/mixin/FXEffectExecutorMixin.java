package com.ombremoon.spellbound.mixin;

import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.ombremoon.spellbound.client.photon.ConfigurableEffect;
import com.ombremoon.spellbound.client.photon.converter.ConfigBuilder;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(FXEffectExecutor.class)
public class FXEffectExecutorMixin implements ConfigurableEffect {
    private final List<ConfigBuilder<?>> config = new ArrayList<>();

    @Override
    public List<ConfigBuilder<?>> modifiedConfigs() {
        return List.copyOf(this.config);
    }

    @Override
    public void addModifiedConfig(ConfigBuilder<?> modifiedConfig) {
        this.config.add(modifiedConfig);
    }
}
