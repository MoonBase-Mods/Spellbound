package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBTriggers;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConsumeItemTrigger.class)
public class ConsumeItemTriggerMixin {

    @Inject(method = "trigger", at = @At("TAIL"))
    private void addSpellboundTrigger(ServerPlayer player, ItemStack item, CallbackInfo ci) {
        SBTriggers.CONSUME_ITEM.get().trigger(player, item);
    }
}
