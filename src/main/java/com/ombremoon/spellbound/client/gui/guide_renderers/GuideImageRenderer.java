package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.mojang.math.Axis;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideImage;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.gui.GuiGraphics;

public class GuideImageRenderer implements IPageElementRenderer<GuideImage> {

    @Override
    public void render(GuideImage element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {



        graphics.blit(element.loc(),
                leftPos + element.position().xOffset(),
                topPos + element.position().yOffset(),
                0,
                0,
                element.width(), element.height(),
                element.width(), element.height());

        graphics.blit(CommonClass.customLocation("textures/gui/books/image_borders/grimoire_corner.png"),
                leftPos + element.position().xOffset()-2,
                topPos + element.position().yOffset()-2,
                0, 0,
                20, 18,
                20, 18);

        float xRot = leftPos + element.position().xOffset();
        float yRot = topPos + element.position().yOffset()   ;

        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(90), xRot + element.width() + 2, yRot - 2, 0);

        graphics.blit(CommonClass.customLocation("textures/gui/books/image_borders/grimoire_corner.png"),
                leftPos + element.position().xOffset() + element.width() + 2,
                topPos + element.position().yOffset()-2,
                0, 0,
                20, 18,
                20, 18);

        graphics.pose().rotateAround(Axis.ZN.rotationDegrees(90), xRot + element.width() + 2, yRot - 2, 0);
        graphics.pose().rotateAround(Axis.ZN.rotationDegrees(90), xRot-2, yRot + element.height()+2, 0);

        graphics.blit(CommonClass.customLocation("textures/gui/books/image_borders/grimoire_corner.png"),
                leftPos + element.position().xOffset()-2,
                topPos + element.position().yOffset() + element.height() +2,
                0, 0,
                20, 18,
                20, 18);

        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(90), xRot-2, yRot + element.height()+2, 0);
        graphics.pose().rotateAround(Axis.ZN.rotationDegrees(180), xRot + element.width() + 2, yRot + element.height()+2, 0);

        graphics.blit(CommonClass.customLocation("textures/gui/books/image_borders/grimoire_corner.png"),
                leftPos + element.position().xOffset() + element.width() +2,
                topPos + element.position().yOffset() + element.height() +2,
                0, 0,
                20, 18,
                20, 18);

        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(180), xRot + element.width() + 2, yRot + element.height()+2, 0);

    }
}
