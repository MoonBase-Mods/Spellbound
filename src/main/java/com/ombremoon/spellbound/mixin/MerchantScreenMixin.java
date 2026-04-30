package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.world.entity.SBMerchant;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.MerchantAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> implements MerchantAccessor {
    private static final WidgetSprites DUNGEON_BUTTON_SPRITES = new WidgetSprites(
            CommonClass.customLocation("dungeon/dungeon_key_button"),
            CommonClass.customLocation("dungeon/dungeon_key_button_highlighted")
    );

    @Unique
    private boolean spellbound$isBroker;

    @Unique
    private int spellbound$merchantId;

    @Unique
    private boolean spellbound$isRiddle;

    public MerchantScreenMixin(MerchantMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void spellbound$init(CallbackInfo ci) {
        if (this.spellbound$isBroker()) {
            this.spellbound$initializeBrokerButton();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void spellbound$render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        MerchantAccessor access = (MerchantAccessor)this.menu;
        if (access.spellbound$isBroker()) {
            this.spellbound$setBroker(true);
            this.spellbound$setMerchantId(access.spellbound$getMerchantId());
            this.spellbound$initializeBrokerButton();
            access.spellbound$setBroker(false);
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private ItemStack renderMysteryItem(ItemStack stack) {
        if (this.spellbound$isRiddle) {
            return new ItemStack(SBItems.DEBUG_ITEM.get());
        }

        return stack;
    }

    private void spellbound$initializeBrokerButton() {
        this.addRenderableWidget(new ImageButton(this.leftPos + 245, this.height / 2 - 78, 20, 20, DUNGEON_BUTTON_SPRITES, p_313433_ -> {
            Entity entity = this.minecraft.level.getEntity(this.spellbound$getMerchantId());
            if (entity instanceof SBMerchant merchant) {
                this.spellbound$isRiddle = !merchant.getTradeType();
                this.menu.setOffers(new MerchantOffers());
                PayloadHandler.setBrokerTrades(this.menu.containerId, merchant.getId(), !merchant.getTradeType());
            }
        }));
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
