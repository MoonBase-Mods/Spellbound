package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;

public class MagicShieldItem extends ShieldItem {
    public MagicShieldItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var handler = SpellUtil.getSpellHandler(player);
        handler.parryTick = level.getGameTime();
        return super.use(level, player, hand);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(SBItems.MANA_BOTTLE.get());
    }
}
