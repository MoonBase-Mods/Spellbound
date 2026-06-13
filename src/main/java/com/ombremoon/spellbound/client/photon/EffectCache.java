package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.lowdragmc.photon.client.fx.FXRuntime;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectCache {
    private final Map<ResourceLocation, FXEffectExecutor> cache = new Object2ObjectOpenHashMap<>();

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

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    public static <T> FXEffectExecutor getFX(T object, ResourceLocation location, Map<T, ? extends List<? extends FXEffectExecutor>> cache) {
        var list = cache.get(object);
        if (list != null && !list.isEmpty()) {
            for (var effect : list) {
                if (location.equals(effect.getFx().getFxLocation()))
                    return effect;
            }
        }

        return null;
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

    protected void removeFX(FXEffectExecutor effect, boolean removeObjects) {
        List<? extends FXEffectExecutor> currentEffects = null;
        switch (effect) {
            case EntityEffectExecutor entityEffect -> currentEffects = EntityEffectExecutor.CACHE.get(entityEffect.entity);
            case BlockEffectExecutor blockEffect -> currentEffects = BlockEffectExecutor.CACHE.get(blockEffect.pos);
            case CustomEffectExecutor<?> customEffect -> currentEffects = customEffect.getEffects();
            default -> {
            }
        }

        if (currentEffects == null) return;
        List<FXEffectExecutor> snapshot = new ArrayList<>(currentEffects);
        for (FXEffectExecutor fx : snapshot) {
            FXRuntime runtime = fx.getRuntime();
            ResourceLocation location = effect.getFx().getFxLocation();
            if (location == null || location.equals(fx.getFx().getFxLocation())) {
                currentEffects.remove(fx);
                this.removeFX(effect);
                if (removeObjects && runtime != null)
                    runtime.destroy(false);
            }
        }

        switch (effect) {
            case EntityEffectExecutor entityEffect -> EntityEffectExecutor.CACHE.put(entityEffect.entity, (List<EntityEffectExecutor>) currentEffects);
            case BlockEffectExecutor blockEffect -> BlockEffectExecutor.CACHE.put(blockEffect.pos, (List<BlockEffectExecutor>) currentEffects);
            case CustomEffectExecutor<?> customEffect -> customEffect.removeEffect();
            default -> {
            }
        }
    }
}
