package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DivineCharmItem extends Item {
    public DivineCharmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (interactionTarget instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (interactionTarget.isAlive() && interactionTarget.getData(SBData.DIVINE_CHARM) != player.getId() && owner != null && player.is(owner)) {
                interactionTarget.level().playSound(player, interactionTarget, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.level().isClientSide) {
                    interactionTarget.setData(SBData.DIVINE_CHARM, player.getId());
                    stack.shrink(1);
                }

                return InteractionResult.sidedSuccess(player.level().isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        RenderUtil.renderJudgementTooltip(stack, context, tooltipComponents, tooltipFlag);
    }
}
