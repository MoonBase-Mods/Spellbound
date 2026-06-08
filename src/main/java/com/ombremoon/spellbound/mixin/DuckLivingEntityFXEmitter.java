package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.client.photon.EffectCache;
import com.ombremoon.spellbound.client.photon.FXEmitter;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LivingEntity.class, remap = false)
public class DuckLivingEntityFXEmitter implements FXEmitter {
    private final EffectCache effectCache = new EffectCache();

    @Override
    public EffectCache getFXCache() {
        return this.effectCache;
    }
}
