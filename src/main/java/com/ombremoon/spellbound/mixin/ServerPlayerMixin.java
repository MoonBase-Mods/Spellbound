package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "triggerRecipeCrafted", at = @At("TAIL"))
    private void addSpellboundTrigger(RecipeHolder<?> recipe, List<ItemStack> items, CallbackInfo ci) {
        SBTriggers.RECIPE_CRAFTED.get().trigger(spellbound$self(), recipe.id(), items);
    }

    private ServerPlayer spellbound$self() {
        return (ServerPlayer) (Object) this;
    }
}
