package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "mayPickup", at = @At("RETURN"), cancellable = true)
    private void disableImbuementPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = this.getItem();
        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        if (imbuement != null && imbuement.spellType().isPresent()) {
            cir.setReturnValue(false);
        }
    }
}
