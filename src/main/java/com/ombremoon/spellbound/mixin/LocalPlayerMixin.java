package com.ombremoon.spellbound.mixin;

import com.mojang.authlib.GameProfile;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer {

    @Shadow
    @Final
    public ClientPacketListener connection;

    @Shadow
    public Input input;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void sendInputPacket(CallbackInfo ci) {
        if (this.level().hasChunkAt(this.getBlockX(), this.getBlockZ())) {
            var handler = SpellUtil.getSpellHandler(this);
            if (handler.inCastMode()) {
                this.connection.send(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyDown));
            }
        }
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void dropMixin(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = this.getMainHandItem();
        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        if (imbuement != null && imbuement.spellType().isPresent()) {
            cir.setReturnValue(false);
        }
    }

}
