package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.world.block.ChalkBoardBlock;
import com.ombremoon.spellbound.common.world.block.StarMapBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarmapBlockItem extends BlockItem {

    public StarmapBlockItem() {
        super(SBBlocks.STAR_MAP.get(), new Properties());
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return state;
        ItemStack stack = context.getItemInHand();

        if (!stack.has(SBData.STARMAP_TYPE) || stack.get(SBData.STARMAP_TYPE) == null) return state;

        state = state.setValue(StarMapBlock.TYPE, stack.get(SBData.STARMAP_TYPE));
        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (!stack.has(SBData.STARMAP_TYPE) || stack.get(SBData.STARMAP_TYPE) == null) return;
        tooltipComponents.add(Component.literal(stack.get(SBData.STARMAP_TYPE).getSerializedName()));
    }
}
