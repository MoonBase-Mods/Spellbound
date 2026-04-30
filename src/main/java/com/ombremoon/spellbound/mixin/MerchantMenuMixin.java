package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.world.entity.SBMerchant;
import com.ombremoon.spellbound.util.MerchantAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin implements MerchantAccessor {

    @Shadow
    @Final
    public Merchant trader;
    @Unique
    private boolean spellbound$isBroker;

    @Unique
    private int spellbound$merchantId;

    @Inject(method = "removed", at = @At("TAIL"))
    private void spellbound$removed(Player player, CallbackInfo ci) {
        if (!this.trader.isClientSide() && this.trader instanceof SBMerchant merchant) {
            merchant.setTradeType(false);
        }
    }

    @Override
    public boolean spellbound$isBroker() {
        return spellbound$isBroker;
    }

    @Override
    public void spellbound$setBroker(boolean isBroker) {
        this.spellbound$isBroker = isBroker;
    }

    @Override
    public int spellbound$getMerchantId() {
        return spellbound$merchantId;
    }

    @Override
    public void spellbound$setMerchantId(int id) {
        this.spellbound$merchantId = id;
    }
}
