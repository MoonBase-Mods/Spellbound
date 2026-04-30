package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {

    @Inject(method = "onTake", at = @At("TAIL"))
    private void spellbound$onTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (stack.is(SBItems.DUNGEON_KEY.get())) {
            stack.set(SBData.ENCRYPTED_KEY, false);
        }
    }
}
