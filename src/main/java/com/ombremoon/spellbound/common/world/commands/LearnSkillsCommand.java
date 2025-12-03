package com.ombremoon.spellbound.common.world.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class LearnSkillsCommand {

    public LearnSkillsCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("learnskills")
                .then(Commands.argument("spells", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                        .executes(cmdContext -> learnSpells(cmdContext.getSource(),
                                ResourceArgument.getResource(cmdContext, "spells", SBSpells.SPELL_TYPE_REGISTRY_KEY))))
                .then(Commands.argument("spells", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                        .then(Commands.literal("reset")
                                .executes(cmdContext -> resetSpells(cmdContext.getSource(),
                                        ResourceArgument.getResource(cmdContext, "spells", SBSpells.SPELL_TYPE_REGISTRY_KEY)))))
                .then(Commands.argument("spells", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                        .then(Commands.argument("skill", ResourceArgument.resource(context, SBSkills.SKILL_REGISTRY_KEY))
                                .executes(cmdContext -> learnSingleSkill(cmdContext.getSource(),
                                        ResourceArgument.getResource(cmdContext, "skill", SBSkills.SKILL_REGISTRY_KEY))))));
    }

    private int learnSingleSkill(CommandSourceStack context, Holder.Reference<Skill> skill) {
        if (!context.isPlayer()) return 0;

        if (!SpellUtil.getSpellHandler(context.getPlayer()).getSpellList().contains(skill.value().getSpell())) {
            context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spellunknown",
                    skill.value().getSpell().createSpell().getName()));
            return 0;
        }

        var skillHandler = SpellUtil.getSkills(context.getPlayer());
        skillHandler.unlockSkill(skill.value(), false);
        skillHandler.sync();
        context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.singleskilllearnt",
                skill.value().getName()));

        return 1;
    }

    private int learnSpells(CommandSourceStack context, Holder.Reference<SpellType<?>> spell) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());
        SkillHolder skillHolder = SpellUtil.getSkills(context.getPlayer());

        SpellType<?> spellType = SBSpells.REGISTRY.get(spell.key());
        if (!handler.getSpellList().contains(spellType)) {
            context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.spellunknown",
                    spell.value().createSpell().getName()));
            return 0;
        };

        for (Skill skill : spellType.getSkills()) {
            skillHolder.unlockSkill(skill, false);
        }

        skillHolder.sync();
        context.getPlayer().sendSystemMessage(Component.translatable("command.spellbound.learntskills",
                spell.value().createSpell().getName()));

        return 1;
    }

    private int resetSpells(CommandSourceStack context, Holder.Reference<SpellType<?>> spell) {
        if (!context.isPlayer()) return 0;
        SpellHandler handler = SpellUtil.getSpellHandler(context.getPlayer());
        SkillHolder skillHolder = SpellUtil.getSkills(context.getPlayer());

        SpellType<?> spellType = SBSpells.REGISTRY.get(spell.key());
        if (!handler.getSpellList().contains(spellType)) return 0;

        skillHolder.resetSkills(spellType);
        skillHolder.sync();
        return 1;
    }
}
