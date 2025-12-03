package com.ombremoon.spellbound.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
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
        Player player = Minecraft.getInstance().player;
        var handler = SpellUtil.getSpellHandler(player);
        if (handler.inCastMode())
            renderCastMode(guiGraphics, player, handler);

        renderActiveSpells(guiGraphics, handler);
    }

    private void renderCastMode(GuiGraphics guiGraphics, Player player, SpellHandler caster) {
        int x = guiGraphics.guiWidth() - 27;
        int y = 30;
        guiGraphics.blit(MANA_BAR, guiGraphics.guiWidth() / 2 - 200, guiGraphics.guiHeight() - 20, 0, 0, 106, 16, 106, 28);
        guiGraphics.blit(MANA_BAR, guiGraphics.guiWidth() / 2 - 198, guiGraphics.guiHeight() - 15, 2, 18, RenderUtil.getScaledRender((int)Math.floor(caster.getMana()), (int)Math.floor(player.getAttributeValue(SBAttributes.MAX_MANA)), 103), 8, 106, 28);
        int mana = Mth.floor(caster.getMana());
        guiGraphics.drawString(Minecraft.getInstance().font,
                mana + "/" + Mth.floor(caster.getMaxMana()),
                40,
                guiGraphics.guiHeight() - 40,
                8889187 ,
                false);

        SpellType<?> spell = caster.getSelectedSpell();
        if (spell == null) return;

        ResourceLocation texture = spell.createSpell().getTexture();
        guiGraphics.blit(texture, x, y, 0, 0, 24, 24, 24, 24);
        guiGraphics.blit(BACKGROUND, x - 1, y - 1, 0, 0, 26, 26, 26, 26);
        guiGraphics.drawString(Minecraft.getInstance().font,
                spell.createSpell().getName(),
                40, guiGraphics.guiHeight() - 60,
                8889187, false);
    }

    private void renderActiveSpells(GuiGraphics guiGraphics, SpellHandler handler) {
        Set<AbstractSpell> spells = new ObjectOpenHashSet<>(
                handler.getActiveSpells()
                        .stream()
                        .filter(spell -> spell.shouldRender(spell.getContext()))
                        .sorted(Comparator.comparing(AbstractSpell::location))
                        .toList());
        RenderSystem.enableBlend();
        int j1 = 0;
        SpellType<?> spellType = null;
        List<Runnable> list = Lists.newArrayListWithExpectedSize(spells.size());

        for (AbstractSpell spell : spells) {
            int i = 1;
            int j = 1;
            if (spellType != spell.spellType()) {
                spellType = spell.spellType();
            }

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
            list.add(() -> {
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, f1);
                guiGraphics.blit(spell.getTexture(), l1, i1, 0, 0, 24, 24, 24, 24);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            });
        }

        list.forEach(Runnable::run);
        RenderSystem.disableBlend();
    }
}
