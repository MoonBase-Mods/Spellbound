package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideSpellBorderElement;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.Optional;

public class GuideSpellBorderRenderer implements IPageElementRenderer<GuideSpellBorderElement> {
    @Override
    public void render(GuideSpellBorderElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        SpellPath path = element.path();
        Optional<SpellMastery> mastery = element.mastery();
        Font font = Minecraft.getInstance().font;
        topPos += 15;

        graphics.blit(
                element.pathTexture(),
                leftPos - 7 + element.position().xOffset(),
                topPos + element.position().yOffset(),
                0, 0,
                16, 16, 16, 16
        );

        graphics.hLine(leftPos + element.position().xOffset() + 8, leftPos + element.position().xOffset() + 150, topPos + element.position().yOffset() + 8, element.colour());

        topPos -= 5;

        if (mastery.isPresent()) {
            String masteryText = I18n.get("guide.element.spell_border.mastery", mastery.get().toString());
            graphics.drawString(Minecraft.getInstance().font,
                    masteryText,
                    ((153 - font.width(masteryText)) / 2) + leftPos + element.position().xOffset(),
                    topPos + 168 + element.position().yOffset(), element.colour(), false);
        }

        graphics.blit(
                element.pathTexture(),
                leftPos + 135 + element.position().xOffset(),
                topPos + 170,
                0, 0,
                16, 16, 16, 16
        );

        graphics.hLine(leftPos + element.position().xOffset() + 8, leftPos + element.position().xOffset() + 135, topPos+178, element.colour());

        if (path.isSubPath()) {
            String elementText = I18n.get("guide.element.spell_border.element");

            int textStart = (153 - font.width(elementText + path.getSerializedName())) / 2;
            graphics.drawString(font,
                    elementText,
                    textStart + leftPos + element.position().xOffset(),
                    topPos + 182 + element.position().yOffset(), element.colour(), false);
            graphics.drawString(font,
                    path.name(),
                    textStart + font.width(elementText) + leftPos + element.position().xOffset(),
                    topPos + 182 + element.position().yOffset(), path.getColor(), false);
        }
    }
}
