package com.ombremoon.spellbound.common.world.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class LearnSpellCommand {

    public LearnSpellCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("learnspell")
                .then(Commands.argument("spells", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                        .executes(cmdContext -> learnSpell(cmdContext.getSource(),
                                ResourceArgument.getResource(cmdContext, "spells", SBSpells.SPELL_TYPE_REGISTRY_KEY))))
                .then(Commands.argument("spells", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                        .then(Commands.literal("forget")
                                .executes(cmdContext -> forgetSpell(cmdContext.getSource(),
                                        ResourceArgument.getResource(cmdContext, "spells", SBSpells.SPELL_TYPE_REGISTRY_KEY))))));
    }

    private int learnSpell(CommandSourceStack context, Holder.Reference<SpellType<?>> spellType) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());

        if (handler.getSpellList().contains(spellType.value())) {
            context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.alreadyknown",
                    spellType.value().createSpell().getName()));
            return 0;
        }

        handler.learnSpell(spellType.value());
        handler.sync();
        context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spelllearnt",
                spellType.value().createSpell().getName()));

        return 1;
    }

    private int forgetSpell(CommandSourceStack context, Holder.Reference<SpellType<?>> spellType) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());

        if (!handler.getSpellList().contains(spellType.value())) {
            context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spellunknown",
                    spellType.value().createSpell().getName()));
            return 0;
        }

        handler.removeSpell(spellType.value());
        handler.sync();
        context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spellforgot",
                spellType.value().createSpell().getName()));

        return 1;
    }
}
