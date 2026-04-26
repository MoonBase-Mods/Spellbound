package com.ombremoon.spellbound.common.world.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class TestImbuementCommand {
    private static final ResourceLocation DEFAULT_FX = CommonClass.customLocation("charged_status");

    public TestImbuementCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sb_test_imbuement")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> apply(ctx.getSource(), DEFAULT_FX))
                .then(Commands.argument("fx", ResourceLocationArgument.id())
                        .executes(ctx -> apply(ctx.getSource(), ResourceLocationArgument.getId(ctx, "fx"))))
                .then(Commands.literal("clear")
                        .executes(ctx -> clear(ctx.getSource()))));
    }

    private int apply(CommandSourceStack src, ResourceLocation fxLoc) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty()) {
            src.sendFailure(Component.literal("Hold an item in your main hand."));
            return 0;
        }
        stack.set(SBData.IMBUEMENT_FX_OVERRIDE.get(), fxLoc);
        src.sendSuccess(() -> Component.literal("Applied test imbuement FX " + fxLoc), false);
        return 1;
    }

    private int clear(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        stack.remove(SBData.IMBUEMENT_FX_OVERRIDE.get());
        src.sendSuccess(() -> Component.literal("Cleared test imbuement FX"), false);
        return 1;
    }
}
