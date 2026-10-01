package com.ombremoon.spellbound.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.skills.SkillProvider;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class CastModeOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation BACKGROUND = CommonClass.customLocation("textures/gui/spell_background.png");
    private static final ResourceLocation MANA_BAR = CommonClass.customLocation("textures/gui/mana_bar.png");

    public CastModeOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        render(guiGraphics, deltaTracker);
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui)
            return;

        Player player = minecraft.player;
        var handler = SpellUtil.getSpellHandler(player);
        if (handler.inCastMode())
            renderCastMode(guiGraphics, minecraft, player, handler);

        renderActiveSpells(guiGraphics, handler);
    }

    private void renderCastMode(GuiGraphics guiGraphics, Minecraft minecraft, Player player, SpellHandler caster) {
        int x = guiGraphics.guiWidth() / 2 - 205;
        int y = guiGraphics.guiHeight() - 57;
        guiGraphics.blit(MANA_BAR, guiGraphics.guiWidth() / 2 - 200, guiGraphics.guiHeight() - 20, 0, 0, 106, 16, 106, 28);
        guiGraphics.blit(MANA_BAR, guiGraphics.guiWidth() / 2 - 198, guiGraphics.guiHeight() - 15, 2, 18, RenderUtil.getScaledRender((int)Math.floor(caster.getMana()), (int)Math.floor(player.getAttributeValue(SBAttributes.MAX_MANA)), 103), 8, 106, 28);
        int mana = Mth.floor(caster.getMana());
        guiGraphics.drawString(Minecraft.getInstance().font,
                mana + "/" + Mth.floor(caster.getMaxMana()),
                guiGraphics.guiWidth() / 2 - 174,
                guiGraphics.guiHeight() - 40,
                8889187 ,
                false);

        SpellType<?> spell = caster.getSelectedSpell();
        if (spell == null) return;

        ResourceLocation texture = this.getSpellTexture(player, spell.createSpell());
        guiGraphics.blit(texture, x, y, 0, 0, 24, 24, 24, 24);
        guiGraphics.blit(BACKGROUND, x - 2, y - 2, 0, 0, 28, 28, 28, 28);

        var skills = SpellUtil.getSkills(player);
        SkillProvider skill = this.getSkillCooldown(player, spell.createSpell());
        float f = skills.getCooldowns().getCooldownPercent(skill, minecraft.getTimer().getGameTimeDeltaPartialTick(true));
        if (f > 0.0F) {
            int i1 = y + Mth.floor(24.0F * (1.0F - f));
            int j1 = i1 + Mth.ceil(24.0F * f);
            guiGraphics.fill(RenderType.guiOverlay(), x, i1, x + 24, j1, Integer.MAX_VALUE);
        }

        guiGraphics.drawString(Minecraft.getInstance().font,
                this.getSpellName(player, spell.createSpell()),
                guiGraphics.guiWidth() / 2 - 173, guiGraphics.guiHeight() - 57,
                8889187, false);
    }

    private SkillProvider getSkillCooldown(Player player, AbstractSpell spell) {
        if (spell instanceof RadialSpell) {
            var skills = SpellUtil.getSkills(player);
            return skills.getChoice(spell.spellType());
        }

        return spell.spellType().getRootSkill();
    }

    private Component getSpellName(Player player, AbstractSpell spell) {
        if (spell instanceof RadialSpell) {
            var skills = SpellUtil.getSkills(player);
            SkillProvider skill = skills.getChoice(spell.spellType());
            return skill.getName();
        }
        return spell.getName();
    }

    private ResourceLocation getSpellTexture(Player player, AbstractSpell spell) {
        if (spell instanceof RadialSpell) {
            var skills = SpellUtil.getSkills(player);
            SkillProvider skill = skills.getChoice(spell.spellType());
            return skill.getTexture();
        }
        return spell.getTexture();
    }

    private void renderActiveSpells(GuiGraphics guiGraphics, SpellHandler handler) {
        // Group spells by type and count instances
        var groupedSpells = handler.getActiveSpells()
                .stream()
                .filter(AbstractSpell::shouldRender)
                .collect(Collectors.groupingBy(
                        AbstractSpell::spellType,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream().sorted(Comparator.comparing(AbstractSpell::location)).toList()
                        )
                ));

        // Sort spell types by the location of their first spell
        var sortedSpellTypes = groupedSpells.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().get(0).location()))
                .toList();

        RenderSystem.enableBlend();
        int j1 = 0;
        List<Runnable> list = Lists.newArrayListWithExpectedSize(sortedSpellTypes.size());

        for (var entry : sortedSpellTypes) {
            List<AbstractSpell> spellsOfType = entry.getValue();
            AbstractSpell spell = spellsOfType.getFirst(); // Use first spell as representative
            int count = spellsOfType.size();

            int i = 1;
            int j = 1;

            i += 2 + (27 * j1);
            j1++;
            float f = 1.0F;
            guiGraphics.blit(BACKGROUND, i, j, 0, 0, 26, 26, 26, 26);
            if (spell.getDuration() - spell.tickCount < 200 && spell.getCastType() != AbstractSpell.CastType.CHANNEL) {
                int k = spell.tickCount;
                int l = 10 - k / 20;
                f = Mth.clamp((float)k / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                        + Mth.cos((float)k * (float) Math.PI / 5.0F) * Mth.clamp((float)l / 10.0F * 0.25F, 0.0F, 0.25F);
            }

            int l1 = i + 1;
            int i1 = j + 1;
            float f1 = f;
            int finalI = i;
            int finalJ = j;
            list.add(() -> {
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, f1);
                guiGraphics.blit(spell.getTexture(), l1, i1, 0, 0, 24, 24, 24, 24);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                // Render count if more than one spell of this type
                if (count > 1) {
                    String countText = String.valueOf(count);
                    int textX = finalI + 26 - Minecraft.getInstance().font.width(countText) - 2;
                    int textY = finalJ + 26 - Minecraft.getInstance().font.lineHeight;
                    guiGraphics.drawString(Minecraft.getInstance().font, countText, textX, textY, 0xFFFFFF, true);
                }
            });
        }

        list.forEach(Runnable::run);
        RenderSystem.disableBlend();
    }
}
