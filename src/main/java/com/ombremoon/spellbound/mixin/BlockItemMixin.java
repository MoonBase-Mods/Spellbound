package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.magic.acquisition.deception.RuleType;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDungeonData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place", at = @At(value = "HEAD"), cancellable = true)
    private void place(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (PuzzleDungeonData.hasRule(level, player, RuleType.NO_BUILDING, state.getBlock())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
