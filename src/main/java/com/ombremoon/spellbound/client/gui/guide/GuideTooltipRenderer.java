package com.ombremoon.spellbound.client.gui.guide;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideTooltipElement;
import com.ombremoon.spellbound.client.gui.guide.renderers.IPageElementRenderer;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Optional;

public class GuideTooltipRenderer implements IPageElementRenderer<GuideTooltipElement> {
    @Override
    public void render(GuideTooltipElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        if (RenderUtil.isHovering(leftPos, topPos, element.position().xOffset(), element.position().yOffset(), element.width(), element.height(), mouseX, mouseY)) {
            graphics.renderTooltip(Minecraft.getInstance().font, element.tooltips(), Optional.empty(), mouseX, mouseY);
        }
    }
}
