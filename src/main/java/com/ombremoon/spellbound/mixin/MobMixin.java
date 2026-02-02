package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", shift = At.Shift.BEFORE))
    private void mobInteractPre(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        var result = EventFactory.onMobInteractPre(player, self(), hand);
        if (result != null) cir.setReturnValue(result);
    }

    @Inject(method = "interact", at = @At(value = "RETURN"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        var result = EventFactory.onMobInteractPost(player, self(), hand);
        if (result != null) cir.setReturnValue(result);
    }

    @Inject(method = "isSunBurnTick", at = @At("RETURN"), cancellable = true)
    private void isSunBurnTick(CallbackInfoReturnable<Boolean> cir) {
        if (SpellUtil.isSummon(self())) {
            cir.setReturnValue(false);
        }
    }

    private Mob self() {
        return (Mob) (Object) this;
    }
}
