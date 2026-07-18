package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderHearts", at = @At("HEAD"),  cancellable = true)
    private void renderHearts(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo ci) {
        if (player.hasEffect(SBEffects.OBFUSCATED)) ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"),  cancellable = true)
    private void renderFood(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        if (player.hasEffect(SBEffects.OBFUSCATED)) ci.cancel();
    }
}
