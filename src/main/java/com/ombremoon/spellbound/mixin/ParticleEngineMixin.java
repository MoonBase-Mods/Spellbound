package com.ombremoon.spellbound.mixin;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.event.custom.RegisterEffectCacheEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Inject(method = "setLevel", at = @At(value = "RETURN"))
    private void photon$injectSetLevel(ClientLevel level, CallbackInfo ci) {
        for (var cache : RegisterEffectCacheEvent.getCaches()) {
            cache.clear();
        }
    }
}
