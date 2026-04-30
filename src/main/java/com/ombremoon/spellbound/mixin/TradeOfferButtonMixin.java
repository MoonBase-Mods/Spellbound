package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.world.entity.SBMerchant;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.MerchantAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MerchantScreen.TradeOfferButton.class)
public class TradeOfferButtonMixin {

    @ModifyArg(method = "renderToolTip", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"))
    private ItemStack renderMysteryItem(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (mc.screen instanceof MerchantScreen screen) {
            MerchantAccessor access = (MerchantAccessor)screen;
            Entity entity = level.getEntity(access.spellbound$getMerchantId());
            if (entity instanceof SBMerchant merchant && merchant.getTradeType()) {
                return new ItemStack(SBItems.DEBUG_ITEM.get());
            }
        }
        return stack;
    }
}
