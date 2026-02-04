package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBStats;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class SpellTomeItem extends Item {

    public SpellTomeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        SpellType<?> spell = itemStack.get(SBData.SPELL_TOME);
        if (!level.isClientSide) {
            var handler = SpellUtil.getSpellHandler(player);
            if (handler.getSpellList().contains(spell)) {
                player.displayClientMessage(Component.translatable("chat.spelltome.awardxp"), true);
                SpellUtil.getSkills(player).awardSpellXp(spell, 10);
                itemStack.shrink(1);
                return InteractionResultHolder.fail(itemStack);
            }

            player.displayClientMessage(Component.translatable("chat.spelltome.spellunlocked").append(spell.createSpell().getNameId()), true);
            handler.learnSpell(spell);
            if (!player.getAbilities().instabuild)
                itemStack.shrink(1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        player.awardStat(SBStats.SPELLS_LEARNED.get());
        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        SpellType<?> spellType = stack.get(SBData.SPELL_TOME);
        if (spellType == null) {
            tooltipComponents.add(Component.translatable("chat.spelltome.nospell").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (spellType.getRootSkill() == null) {
            tooltipComponents.add(spellType.createSpell().getDescription().withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltipComponents.add(spellType.createSpell().getName().withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.empty());
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.spellbound.holdshift").withStyle(ChatFormatting.BLUE));
        } else {
            tooltipComponents.add(spellType.getRootSkill().getDescription()
                    .withStyle(ChatFormatting.BLUE));
        }
    }

    public static ItemStack createWithSpell(SpellType<?> spellType) {
        ItemStack tome = new ItemStack(SBItems.SPELL_TOME.get());
        tome.set(SBData.SPELL_TOME, spellType);

        return tome;
    }

    private InteractionResultHolder<ItemStack> fail(Player player, ItemStack stack) {
        player.displayClientMessage(Component.translatable("chat.spelltome.nospell"), true);
        return InteractionResultHolder.fail(stack);
    }
}
