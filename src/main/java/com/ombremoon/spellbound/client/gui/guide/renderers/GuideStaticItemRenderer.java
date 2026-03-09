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
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import java.util.Optional;

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

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideStaticItemElement element) {
        float scale = 1.3f * element.extras().scale();
        int size = (int) (16 * 1.2f * scale);
        int x = leftPos + element.position().xOffset();
        int y = topPos + element.position().yOffset();
        return mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size;
    }

    @Override
    public void handleHover(GuideStaticItemElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        if (!isVisible(element.extras().pageScrap())) return;

        ItemStack item = new GuideGhostItem(buildIngredient(element.item()), 0, 0).getItem(ElementRenderDispatcher.getTickCount());
        guiGraphics.renderTooltip(Minecraft.getInstance().font,
                item.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL),
                Optional.empty(),
                mouseX,
                mouseY);
    }
}
