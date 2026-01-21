package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.TransfigurationRitualElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GuideRitualRenderer implements IPageElementRenderer<TransfigurationRitualElement> {

    @Override
    public void render(TransfigurationRitualElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        int itemOffset = element.leftPage() ? 23 : 195;
        int numberOffset = element.leftPage() ? 8 : 280;
        ItemStack item = SBBlocks.TRANSFIGURATION_DISPLAY.get().asItem().getDefaultInstance();
        RenderUtil.renderItem(graphics, item, leftPos + itemOffset - 48, topPos + 20, 5.3F);

        TransfigurationRitual ritual = RitualHelper.getRitualFor(element.ritual());
        ItemStack stack = new GuideGhostItem(buildIngredientFromValues(ritual.clientMaterials()), 30, 30).getItem(tickCount);
        RenderUtil.renderItem(graphics, stack, leftPos + itemOffset + 9, topPos + 22, 2.3F, 200);
        graphics.drawString(Minecraft.getInstance().font,
                Component.literal(String.valueOf(stack.getCount())),
                leftPos + numberOffset,
                topPos + 61,
                0,
                true);

        graphics.blit(CommonClass.customLocation("textures/gui/books/crafting_grids/small/basic.png"),
                leftPos + numberOffset - 7,
                topPos + 56,
                0,
                0,
                17,
                17,
                17,
                17);
    }
}
