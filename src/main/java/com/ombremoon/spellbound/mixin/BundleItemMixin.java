package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBItems;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @Inject(method = "overrideStackedOnOther", at = @At(value = "HEAD"), cancellable = true)
    private void overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (slot.getItem().is(SBItems.SHARD_SATCHEL.get())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "overrideOtherStackedOnMe", at = @At(value = "HEAD"), cancellable = true)
    private void overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
        if (other.is(SBItems.SHARD_SATCHEL.get())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
