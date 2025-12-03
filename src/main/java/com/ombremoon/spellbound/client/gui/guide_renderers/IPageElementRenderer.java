package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.IPageElement;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * This interface should be implemented by any and all element renderers for guide book elements
 */
public interface IPageElementRenderer<T extends IPageElement> {

    Logger LOGGER = Constants.LOG;

    /**
     * The method called when this element is to be rendered on the current page
     * @param graphics the GuiGraphics for renderering
     * @param leftPos the left most x coordinate for the beginning of the page
     * @param topPos the top most y coordinate for the top of the page
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param partialTick the current partial tick value
     */
    void render(T element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick);

    default boolean isVisible(ResourceLocation scrap) {
        return scrap.equals(CommonClass.customLocation("default")) || Minecraft.getInstance().player.isCreative() || SpellUtil.hasScrap(Minecraft.getInstance().player, scrap);
    }
}
