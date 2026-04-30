package com.ombremoon.spellbound.common.world.inventory;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSpells;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;

public class DungeonKeyResultSlot extends MerchantResultSlot {
      public DungeonKeyResultSlot(Player player, Merchant merchant, MerchantContainer slots, int slot, int xPosition, int yPosition) {
        super(player, merchant, slots, slot, xPosition, yPosition);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);
        Boolean bool = stack.get(SBData.ENCRYPTED_KEY);
        if (stack.is(SBItems.DUNGEON_KEY.get()) && bool != null && bool) {
            stack.set(SBData.ENCRYPTED_KEY, false);
        }
    }
}
