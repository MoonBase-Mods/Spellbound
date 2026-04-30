package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "canSpawnSprintParticle", at = @At("RETURN"), cancellable = true)
    private void disableSprintParticles(CallbackInfoReturnable<Boolean> cir) {
        if (this.self() instanceof LivingEntity living && living.hasEffect(SBEffects.MAGI_INVISIBILITY))
            cir.setReturnValue(false);
    }

    private Entity self() {
        return (Entity) (Object) this;
    }
}
