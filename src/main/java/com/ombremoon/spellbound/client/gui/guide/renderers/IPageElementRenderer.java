package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.IPageElement;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface should be implemented by any and all element renderers for guide book elements
 */
public interface IPageElementRenderer<T extends IPageElement> extends Loggable {

    Logger LOGGER = Constants.LOG;

    /**
     * The method called when this element is to be rendered on the current page
     * @param graphics the GuiGraphics for rendering
     * @param leftPos the left most x coordinate for the beginning of the page
     * @param topPos the top most y coordinate for the top of the page
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param partialTick the current partial tick value
     */
    void render(T element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount);

    default boolean isVisible(ResourceLocation scrap) {
        return scrap.equals(CommonClass.customLocation("default")) || Minecraft.getInstance().player.isCreative() || SpellUtil.hasScrap(Minecraft.getInstance().player, scrap);
    }

    default void handleClick(T element, Screen screen) {}

    default void handleHover(T element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {}

    default boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, T element) {
        return false;
    }

    default Ingredient buildIngredient(List<Ingredient> ingredients) {
        List<ItemLike> list = new ArrayList<>();
        for (Ingredient ing : ingredients) {
            for (ItemStack stack : ing.getItems()) {
                list.add(stack.getItem());
            }
        }

        return Ingredient.of(list.toArray(new ItemLike[]{}));
    }

    default Ingredient buildIngredientFromValues(List<TransfigurationRitual.Value> ingredients) {
        List<ItemStack> list = new ArrayList<>();
        for (var value : ingredients) {
            Ingredient ingredient = value.ingredient();
            for (ItemStack stack : ingredient.getItems()) {
                stack.setCount(value.count());
                list.add(stack);
            }
        }

        return Ingredient.of(list.stream());
    }
}
