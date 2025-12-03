package com.ombremoon.spellbound.client.event.custom;

import com.ombremoon.spellbound.client.particle.CustomEffectExecutor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

public class RegisterEffectCacheEvent extends Event implements IModBusEvent {
    private static final List<Map<?, ? extends List<? extends CustomEffectExecutor<?>>>> CACHES = new ObjectArrayList<>();

    @ApiStatus.Internal
    public RegisterEffectCacheEvent() {

    }

    public void registerEffectCache(Map<?, ? extends List<? extends CustomEffectExecutor<?>>> map) {
        CACHES.add(map);
    }

    public static List<Map<?, ? extends List<? extends CustomEffectExecutor<?>>>> getCaches() {
        return CACHES;
    }
}
