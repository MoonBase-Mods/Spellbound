package com.ombremoon.spellbound.common.world.inventory;

import com.ombremoon.spellbound.common.init.SBMenuTypes;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class RiddleTradeMenu extends AbstractContainerMenu {
    private final Merchant trader;
    private final MerchantContainer tradeContainer;
    private int merchantId;

    public RiddleTradeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ClientSideMerchant(playerInventory.player));
    }

    public RiddleTradeMenu(int containerId, Inventory playerInventory, Merchant trader) {
        super(SBMenuTypes.RIDDLE_TRADES.get(), containerId);
        this.trader = trader;
        this.tradeContainer = new MerchantContainer(trader);
        this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
        this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
        this.addSlot(new DungeonKeyResultSlot(playerInventory.player, trader, this.tradeContainer, 2, 220, 37));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 108 + k * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged(container);
    }

    public void setSelectionHint(int currentRecipeIndex) {
        this.tradeContainer.setSelectionHint(currentRecipeIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is null for the initial slot that was double-clicked.
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return false;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
                this.playTradeSound();
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    private void playTradeSound() {
        if (!this.trader.isClientSide()) {
            Entity entity = (Entity)this.trader;
            entity.level()
                    .playLocalSound(entity.getX(), entity.getY(), entity.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
        }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
//        this.trader.setTradingPlayer(null);
        if (!this.trader.isClientSide()) {
            if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
                ItemStack itemstack = this.tradeContainer.removeItemNoUpdate(0);
                if (!itemstack.isEmpty()) {
                    player.drop(itemstack, false);
                }

                itemstack = this.tradeContainer.removeItemNoUpdate(1);
                if (!itemstack.isEmpty()) {
                    player.drop(itemstack, false);
                }
            } else if (player instanceof ServerPlayer) {
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(0));
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(1));
            }
        }
    }

    public void tryMoveItems(int selectedMerchantRecipe) {
        if (selectedMerchantRecipe >= 0 && this.getOffers().size() > selectedMerchantRecipe) {
            ItemStack itemstack = this.tradeContainer.getItem(0);
            if (!itemstack.isEmpty()) {
                if (!this.moveItemStackTo(itemstack, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(0, itemstack);
            }

            ItemStack itemstack1 = this.tradeContainer.getItem(1);
            if (!itemstack1.isEmpty()) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(1, itemstack1);
            }

            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                MerchantOffer merchantoffer = this.getOffers().get(selectedMerchantRecipe);
                this.moveFromInventoryToPaymentSlot(0, merchantoffer.getItemCostA());
                merchantoffer.getItemCostB().ifPresent(p_330236_ -> this.moveFromInventoryToPaymentSlot(1, p_330236_));
            }
        }
    }

    private void moveFromInventoryToPaymentSlot(int paymentSlotIndex, ItemCost payment) {
        for (int i = 3; i < 39; i++) {
            ItemStack itemstack = this.slots.get(i).getItem();
            if (!itemstack.isEmpty() && payment.test(itemstack)) {
                ItemStack itemstack1 = this.tradeContainer.getItem(paymentSlotIndex);
                if (itemstack1.isEmpty() || ItemStack.isSameItemSameComponents(itemstack, itemstack1)) {
                    int j = itemstack.getMaxStackSize();
                    int k = Math.min(j - itemstack1.getCount(), itemstack.getCount());
                    ItemStack itemstack2 = itemstack.copyWithCount(itemstack1.getCount() + k);
                    itemstack.shrink(k);
                    this.tradeContainer.setItem(paymentSlotIndex, itemstack2);
                    if (itemstack2.getCount() >= j) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * {@link net.minecraft.client.multiplayer.ClientPacketListener} uses this to set offers for the client side MerchantContainer.
     */
    public void setOffers(MerchantOffers offers) {
        this.trader.overrideOffers(offers);
    }

    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }

    public void setMerchantId(int id) {
        this.merchantId = id;
    }

        public int getMerchantId() {
            return this.merchantId;
        }
}
