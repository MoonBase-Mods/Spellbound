package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBTriggers;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractTrigger.class)
public class PlayerInteractTriggerMixin {

    @Inject(method = "trigger", at = @At("TAIL"))
    private void addSpellboundTrigger(ServerPlayer player, ItemStack item, Entity entity, CallbackInfo ci) {
        SBTriggers.USE_ITEM_ON_ENTITY.get().trigger(player, item, entity);
    }
}
