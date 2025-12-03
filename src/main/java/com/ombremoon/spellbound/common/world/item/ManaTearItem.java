package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ManaTearItem extends Item {

    public ManaTearItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            AttributeInstance attributeInstance = player.getAttribute(SBAttributes.MAX_MANA);
            attributeInstance.setBaseValue(attributeInstance.getBaseValue() + 100);
            var caster = SpellUtil.getSpellHandler(player);
            caster.awardMana((float) caster.getMaxMana());
            player.awardStat(Stats.ITEM_USED.get(this));
            itemStack.consume(1, player);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
