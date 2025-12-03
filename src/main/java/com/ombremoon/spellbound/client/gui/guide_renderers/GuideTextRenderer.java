package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class GuideTextRenderer implements IPageElementRenderer<GuideText> {

    @Override
    public void render(GuideText element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        List<FormattedCharSequence> lines = font.split(
                Component.translatable(
                        element.translationKey())
                        .withStyle(
                                isVisible(element.extras().pageScrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                element.extras().maxLineLength());

        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(font, lines.get(i), leftPos + element.position().xOffset(), topPos + element.position().yOffset() + (i * 9), element.extras().colour(), element.extras().dropShadow());
        }
    }
}
