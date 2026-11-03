package com.ombremoon.spellbound.mixin;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.ombremoon.spellbound.client.photon.ConfigurableEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEffectExecutor.class)
public abstract class BlockEffectExecutorMixin extends FXEffectExecutor {

    protected BlockEffectExecutorMixin(FX fx, Level level) {
        super(fx, level);
    }

    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lcom/lowdragmc/photon/client/fx/FX;createRuntime()Lcom/lowdragmc/photon/client/fx/FXRuntime;"))
    private FXRuntime modifyRuntime(FX fx) {
        ConfigurableEffect configurableEffect = (ConfigurableEffect) self();
        return configurableEffect.modifyFXRuntime(fx, this.level.registryAccess());
    }

    private BlockEffectExecutor self() {
        return (BlockEffectExecutor) (Object) this;
    }
}
