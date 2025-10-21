package com.ombremoon.spellbound.client.particle;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public abstract class CustomEffectExecutor<T> extends FXEffectExecutor {
    protected final T emitter;

    protected CustomEffectExecutor(T emitter, FX fx, Level level) {
        super(fx, level);
        this.emitter = emitter;
    }

    protected abstract Map<T, ? extends List<? extends FXEffectExecutor>> getCache();

    protected List<? extends FXEffectExecutor> getEffects() {
        return this.getCache().get(this.emitter);
    }

    protected void removeEffect() {
        this.getCache().remove(this.emitter);
    }
}
