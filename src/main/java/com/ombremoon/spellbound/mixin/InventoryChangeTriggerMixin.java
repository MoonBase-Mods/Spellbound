package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryChangeTrigger.class)
public class InventoryChangeTriggerMixin {

    @Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("TAIL"))
    private void addSpellboundTrigger(ServerPlayer player, Inventory inventory, ItemStack stack, int full, int empty, int occupied, CallbackInfo ci) {
        SBTriggers.INVENTORY_CHANGED.get().trigger(player, inventory, stack);
    }
}
