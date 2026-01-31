package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class SpiritWhistleItem extends Item {
    public SpiritWhistleItem(Properties properties) {
        super(properties.durability(100));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var handler = SpellUtil.getFamiliarHandler(context.getPlayer());
        if (handler.hasActiveFamiliar()) {
            handler.discardFamiliar();
            return InteractionResult.CONSUME;
        }

        handler.summonFamiliar(context.getClickedPos());
        context.getItemInHand().setDamageValue(0);
        return super.useOn(context);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }
}
