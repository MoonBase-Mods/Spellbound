package com.ombremoon.spellbound.client.particle;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class EffectCache {
    public final Map<ResourceLocation, FXEffectExecutor> cache = new Object2ObjectOpenHashMap<>();

    public void addFX(EffectBuilder<?> builder) {
        var fx = builder.build();
        if (fx != null) {
            this.cache.put(fx.fx.getFxLocation(), fx);
            fx.start();
        }
    }

    public FXEffectExecutor removeFX(FXEffectExecutor fx) {
        return this.cache.remove(fx.fx.getFxLocation());
    }

    public FXEffectExecutor getFX(ResourceLocation location) {
        return this.cache.get(location);
    }

    public void handleFXRemoval() {
        for (var effect : this.cache.values()) {
            removeFX(effect, false);
        }
    }

    public void removeFX(ResourceLocation location, boolean removeObjects) {
        var effect = this.getFX(location);
        if (effect != null)
            removeFX(effect, removeObjects);
    }

    public void removeFX(FXEffectExecutor effect, boolean removeObjects) {
        var runtime = effect.getRuntime();
        if (runtime != null && runtime.isAlive()) {
            List<? extends FXEffectExecutor> currentEffects = null;
            switch (effect) {
                case EntityEffectExecutor entityEffect -> currentEffects = EntityEffectExecutor.CACHE.get(entityEffect.entity);
                case BlockEffectExecutor blockEffect -> currentEffects = BlockEffectExecutor.CACHE.get(blockEffect.pos);
                case CustomEffectExecutor<?> customEffect -> currentEffects = customEffect.getEffects();
                default -> {
                }
            }

            if (currentEffects == null) return;
            var iter = currentEffects.iterator();
            while (iter.hasNext()) {
                var fx = iter.next();
                ResourceLocation location = effect.getFx().getFxLocation();
                if (location == null || location.equals(fx.getFx().getFxLocation())) {
                    iter.remove();
                    this.removeFX(effect);
                    if (removeObjects)
                        runtime.destroy(true);
                }
            }

            if (currentEffects.isEmpty()) {
                switch (effect) {
                    case EntityEffectExecutor entityEffect -> EntityEffectExecutor.CACHE.remove(entityEffect.entity);
                    case BlockEffectExecutor blockEffect -> BlockEffectExecutor.CACHE.remove(blockEffect.pos);
                    case CustomEffectExecutor<?> customEffect -> customEffect.removeEffect();
                    default -> {
                    }
                }
            }
        }
    }
}
