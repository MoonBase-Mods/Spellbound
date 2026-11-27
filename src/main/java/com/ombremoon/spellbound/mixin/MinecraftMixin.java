package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.client.event.PlayerInputActionEvents;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void handleSpellboundKeybinds(CallbackInfo ci) {
        PlayerInputActionEvents.handleSpellboundKeybinds(spellbound$self(), this.player);
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At("RETURN"), cancellable = true)
    private void shouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        var handler = SpellUtil.getSpellHandler(player);
        if (entity instanceof LivingEntity livingEntity && handler.hasGlowEffect(livingEntity)) {
            cir.setReturnValue(true);
        }
    }

    private Minecraft spellbound$self() {
        return (Minecraft) (Object) this;
    }
}
