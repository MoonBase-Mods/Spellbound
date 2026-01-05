package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideTextListElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GuideTextListRenderer implements IPageElementRenderer<GuideTextListElement> {

    @Override
    public void render(GuideTextListElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        for (int i = 0; i < element.list().size(); i++) {

            int maxRows = element.extras().maxRows();
            int xOffset;
            int yOffset;
            if (maxRows <= 0) {
                xOffset = 0;
                yOffset = i * 20;
            } else {
                xOffset = i >= maxRows ? Math.floorDiv(i, maxRows) * element.extras().columnGap() : 0;
                yOffset = (i >= maxRows ? (i % maxRows) : i) * element.extras().rowGap();
            }


            graphics.drawString(Minecraft.getInstance().font,
                    Component.literal(element.extras().bulletPoint())
                            .append(" ")
                            .append(element.list().get(i))
                            .withStyle(isVisible(element.extras().pageScrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                    leftPos - 10 + element.position().xOffset() + xOffset,
                    topPos + element.position().yOffset() + 6 + yOffset,
                    element.extras().textColour(),
                    element.extras().dropShadow()
            );
        }
    }

}
