package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBTriggers;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameAnimalTrigger.class)
public class TameAnimalMixin {

    @Inject(method = "trigger", at = @At("TAIL"))
    private void addSpellboundTrigger(ServerPlayer player, Animal entity, CallbackInfo ci) {
        SBTriggers.TAME_ANIMAL.get().trigger(player, entity);
    }
}
