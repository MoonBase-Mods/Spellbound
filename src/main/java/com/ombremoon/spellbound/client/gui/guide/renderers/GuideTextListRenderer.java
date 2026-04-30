package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.screens.GuideBookScreen;
import com.ombremoon.spellbound.client.gui.guide.elements.GuideTextListElement;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;

public class GuideTextListRenderer implements IPageElementRenderer<GuideTextListElement> {

    @Override
    public void render(GuideTextListElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        for (int i = 0; i < element.list().size(); i++) {

            int maxRows = element.extras().maxRows();
            int xOffset;
            int yOffset;
            if (maxRows <= 0) {
                xOffset = 0;
                yOffset = i * element.extras().rowGap();
            } else {
                xOffset = i >= maxRows ? Math.floorDiv(i, maxRows) * element.extras().columnGap() : 0;
                yOffset = (i >= maxRows ? (i % maxRows) : i) * element.extras().rowGap();
            }

            var scrapComponent = element.list().get(i);
            int xPos = leftPos + element.position().xOffset() + xOffset;
            int yPos = topPos + element.position().yOffset()  + yOffset + scrapComponent.extraOffset();
            MutableComponent comp = Component.literal(element.extras().bulletPoint())
                    .append(element.extras().bulletPoint().isEmpty() ? "" : " ");

            Style style = Style.EMPTY;
            style = style.applyFormat(isVisible(scrapComponent.scrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED);
            if (!scrapComponent.targetPage().equals(CommonClass.customLocation("default"))
                    && isHoveringItem(xPos, yPos, element, comp.copy().append(scrapComponent.component()), mouseX, mouseY))
                style = style.applyFormat(ChatFormatting.UNDERLINE);

            comp.append(scrapComponent.component()).withStyle(style);

            if (element.extras().centered()) {
                RenderUtil.drawCenteredString(
                        graphics,
                        Minecraft.getInstance().font,
                        comp,
                        xPos, yPos,
                        0
                );
            } else {
                graphics.drawWordWrap(Minecraft.getInstance().font,
                        comp,
                        xPos,
                        yPos,
                        element.extras().lineLength(),
                        element.extras().textColour()
                );
            }
        }
    }

    private boolean isHoveringItem(int leftPos, int topPos, GuideTextListElement element, Component text, int mouseX, int mouseY) {
        return element.extras().underlineClickable()
                && mouseX > leftPos
                && mouseX < leftPos + Minecraft.getInstance().font.width(text)
                && mouseY > topPos
                && mouseY < topPos + Minecraft.getInstance().font.lineHeight ;
    }

    @Override
    public void handleClick(GuideTextListElement element, Screen screen, double mouseX, double mouseY, int leftPos, int topPos) {
        if (!(screen instanceof GuideBookScreen guide)) return;

        int clickXPos = (int) mouseX - leftPos - element.position().xOffset();
        int clickYPos = (int) mouseY - topPos - element.position().yOffset();

        int index = Math.floorDiv(clickYPos, element.extras().rowGap());
        int maxRows = element.extras().maxRows();
        if (maxRows > 0) {
            int column = clickXPos / element.extras().rowGap();
            index += Math.max(column, 0);
        }

        if (index >= element.list().size()) {
            LOGGER.debug("The Index exceeds the list nice one mate.");
            return;
        }

        GuideTextListElement.ScrapComponent entry = element.list().get(index);
        if (entry.targetPage().equals(CommonClass.customLocation("default"))) return;

        guide.setPage(entry.targetPage());
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideTextListElement element) {
        int height = element.extras().maxRows() <= 0
                ? element.list().size() * 20
                : element.extras().maxRows() * element.extras().rowGap();
        int width = element.extras().lineLength();

        int elementLeft = leftPos + element.position().xOffset();
        int elementTop = topPos + element.position().yOffset();

        return mouseX > elementLeft && mouseX < elementLeft + width && mouseY > elementTop && mouseY < elementTop + height;
    }
}
