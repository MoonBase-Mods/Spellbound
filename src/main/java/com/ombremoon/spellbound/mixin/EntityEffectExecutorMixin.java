package com.ombremoon.spellbound.mixin;

import com.lowdragmc.photon.client.fx.*;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.ombremoon.spellbound.client.photon.ConfigurableEffect;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityEffectExecutor.class)
public abstract class EntityEffectExecutorMixin extends FXEffectExecutor {

    protected EntityEffectExecutorMixin(FX fx, Level level) {
        super(fx, level);
    }

    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lcom/lowdragmc/photon/client/fx/FX;createRuntime()Lcom/lowdragmc/photon/client/fx/FXRuntime;"))
    private FXRuntime modifyRuntime(FX fx) {
        ConfigurableEffect configurableEffect = (ConfigurableEffect) self();
        return configurableEffect.modifyFXRuntime(fx, this.level.registryAccess());
    }

    private EntityEffectExecutor self() {
        return (EntityEffectExecutor) (Object) this;
    }
}
