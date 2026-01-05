package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideStaticItemElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GuideStaticItemRenderer implements IPageElementRenderer<GuideStaticItemElement> {
    private RandomSource rand;

    public GuideStaticItemRenderer() {
        this.rand = RandomSource.create(42L);
    }

    @Override
    public void render(GuideStaticItemElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        Registry<Item> registry = BuiltInRegistries.ITEM;

        ItemStack item = isVisible(element.extras().pageScrap())
                ? new GuideGhostItem(buildIngredient(element.item()), 0, 0).getItem(tickCount)
                : registry.getRandom(rand).get().value().getDefaultInstance();

        if (!element.extras().disableBackground()) {
            graphics.blit(CommonClass.customLocation("textures/gui/books/crafting_grids/medium/" + element.tileName() + ".png"),
                    leftPos + element.position().xOffset(),
                    topPos + element.position().yOffset(),
                    0,
                    0,
                    (int) (48 * element.extras().scale()),
                    (int) (46 * element.extras().scale()),
                    (int) (48 * element.extras().scale()),
                    (int) (46 * element.extras().scale()));
        }

        RenderUtil.renderItem(graphics, item, leftPos + element.position().xOffset(), topPos + element.position().yOffset(), 1.3f * element.extras().scale());
    }
}
