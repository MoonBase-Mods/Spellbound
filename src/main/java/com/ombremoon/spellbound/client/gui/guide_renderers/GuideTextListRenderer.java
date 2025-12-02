package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideTextList;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuideTextListRenderer implements IPageElementRenderer<GuideTextList> {

    @Override
    public void render(GuideTextList element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
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
                            .append(Component.translatable(element.list().get(i)))
                            .withStyle(isVisible(element.extras().pageScrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                    leftPos - 10 + element.position().xOffset() + xOffset,
                    topPos + element.position().yOffset() + 6 + yOffset,
                    element.extras().textColour(),
                    element.extras().dropShadow()
            );
        }
    }

}
