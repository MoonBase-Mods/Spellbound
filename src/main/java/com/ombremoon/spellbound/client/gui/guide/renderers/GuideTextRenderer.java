package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideTextElement;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class GuideTextRenderer implements IPageElementRenderer<GuideTextElement> {
    private int maxWidth = 0;
    private int height = 0;

    @Override
    public void render(GuideTextElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        Font font = Minecraft.getInstance().font;

        boolean visible = isVisible(element.extras().pageScrap());
        Style style = Style.EMPTY;
        if (!visible) style = style.applyFormat(ChatFormatting.OBFUSCATED);

        style = style.withBold(element.extras().bold());
        style = style.withItalic(element.extras().italic());
        style = style.withUnderlined(element.extras().underline());

        List<FormattedCharSequence> lines = font.split(
                ((MutableComponent)element.text())
                        .withStyle(style),#
                element.extras().maxLineLength());
        this.height = lines.size() * font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            int width = font.width(lines.get(i));
            if (this.maxWidth < width) this.maxWidth = width;
            if (element.extras().centered()) {
                RenderUtil.drawCenteredString(graphics, font, lines.get(i), leftPos + element.position().xOffset(), topPos + element.position().yOffset() + (i * font.lineHeight), element.extras().colour(), element.extras().dropShadow());
            } else {
                graphics.drawString(font, lines.get(i), leftPos + element.position().xOffset(), topPos + element.position().yOffset() + (i * font.lineHeight), element.extras().colour(), element.extras().dropShadow());
            }
        }

        if (element.extras().scrambledEnd()) {
            int prevEnd = font.width(lines.getLast());
            int yOffset = (lines.size() * font.lineHeight) + topPos + element.position().yOffset();
            int xOffset = leftPos + element.position().xOffset();

            if (element.extras().maxLineLength() - prevEnd >= 5) {
                yOffset -= font.lineHeight;
                xOffset += prevEnd;
            }

            graphics.drawString(font, Component.literal("#####").withStyle(ChatFormatting.OBFUSCATED), xOffset, yOffset, element.extras().colour(), element.extras().dropShadow());
        }
    }

    @Override
    public void handleClick(GuideTextElement element, Screen screen) {
        if (element.extras().link().isBlank()) return;
        Minecraft minecraft = Minecraft.getInstance();
        String url = element.extras().link();

        try {
            URI uri = Util.parseAndValidateUntrustedUri(url);
            if (minecraft.options.chatLinksPrompt().get()) {
                minecraft.setScreen(new ConfirmLinkScreen(p_351659_ -> {
                    if (p_351659_) {
                        Util.getPlatform().openUri(uri);
                    }

                    minecraft.setScreen(screen);
                }, url, true));
            } else {
                Util.getPlatform().openUri(uri);
            }
        } catch (URISyntaxException urisyntaxexception) {
            LOGGER.error("Can't open url for {}", url, urisyntaxexception);
        }
    }

    @Override
    public void handleHover(GuideTextElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {


    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideTextElement element) {
        if (element.extras().hoverText().isBlank()) return false;

        int startX = leftPos + element.position().xOffset();
        int startY = topPos + element.position().yOffset();
        return mouseX > startX && mouseX < startX + this.maxWidth
                && mouseY > startY && mouseY < startY + this.height;
    }
}
