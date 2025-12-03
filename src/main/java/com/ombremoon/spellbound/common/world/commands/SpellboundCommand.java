package com.ombremoon.spellbound.common.world.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class SpellboundCommand {

    public SpellboundCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("spellbound")
                .then(Commands.literal("grantpoint")
                        .then(Commands.argument("spell", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                        .executes(cmdContext -> grantSkillPoint(cmdContext.getSource(),
                                                ResourceArgument.getResource(cmdContext, "spell", SBSpells.SPELL_TYPE_REGISTRY_KEY),
                                                IntegerArgumentType.getInteger(cmdContext, "points"))))))
                .then(Commands.literal("grantpoint")
                        .then(Commands.argument("spell", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                                .executes(cmdContext -> grantSkillPoint(cmdContext.getSource(),
                                        ResourceArgument.getResource(cmdContext, "spell", SBSpells.SPELL_TYPE_REGISTRY_KEY),
                                        1))))
                .then(Commands.literal("mana")
                        .then(Commands.argument("add", IntegerArgumentType.integer())
                                .executes(cmdContext -> addMana(cmdContext.getSource(), IntegerArgumentType.getInteger(cmdContext, "add")))))
                .then(Commands.literal("set_level")
                        .then(Commands.argument("spell", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 5))
                                        .executes(cmdContext -> setSpellLevel(cmdContext.getSource(),
                                                ResourceArgument.getResource(cmdContext, "spell", SBSpells.SPELL_TYPE_REGISTRY_KEY),
                                                IntegerArgumentType.getInteger(cmdContext, "level")))))));
    }

    private int grantSkillPoint(CommandSourceStack context, Holder.Reference<SpellType<?>> spell, int points) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());
        SkillHolder skillHolder = SpellUtil.getSkills(context.getPlayer());

        SpellType<?> spellType = SBSpells.REGISTRY.get(spell.key());
        if (!handler.getSpellList().contains(spellType)) {
            context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spellunknown",
                    spell.value().createSpell().getName()));
            return 0;
        }

        skillHolder.awardSkillPoints(spellType, points);
        skillHolder.sync();
        context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.learntskills",
                spell.value().createSpell().getName()));

        return 1;
    }

    private int setSpellLevel(CommandSourceStack context, Holder.Reference<SpellType<?>> spell, int level) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());
        SkillHolder skillHolder = SpellUtil.getSkills(context.getPlayer());

        SpellType<?> spellType = SBSpells.REGISTRY.get(spell.key());
        if (!handler.getSpellList().contains(spellType)) {
            context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spellunknown",
                    spell.value().createSpell().getName()));
            return 0;
        }

        int spellLevel = skillHolder.getSpellLevel(spellType);
        if (level > SkillHolder.MAX_SPELL_LEVEL || level <= spellLevel) {
            return 0;
        }

        float spellXp = skillHolder.getSpellXp(spellType);
        float totalXp = 0.0F;
        for (int i = spellLevel; i < level; i++) {
            int xpGoal = skillHolder.getXPGoal(i + 1);
            float prevXpGoal = i != spellLevel ? skillHolder.getXPGoal(i) : spellXp;
            float grantedXp = xpGoal - prevXpGoal;
            totalXp += grantedXp;
        }

        skillHolder.awardSpellXp(spellType, totalXp);
        skillHolder.sync();
        return 1;
    }

    private int addMana(CommandSourceStack context, int mana) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());
        handler.awardMana(mana);
        return 1;
    }

    private int setMaxMana(CommandSourceStack context, int mana) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());
        handler.awardMana(mana);
        return 0;
    }
}
