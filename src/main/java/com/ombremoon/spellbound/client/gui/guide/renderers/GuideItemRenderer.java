package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideItemElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.client.gui.guide.renderers.init.ElementRenderDispatcher;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Quaternionf;

import java.util.Optional;

public class GuideItemRenderer implements IPageElementRenderer<GuideItemElement> {

    @Override
    public void render(GuideItemElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        ItemEntity entity = (ItemEntity) getData(element, "entity");
        if (entity == null) {
            entity = EntityType.ITEM.create(Minecraft.getInstance().level);
            saveData(element, "entity", entity);
        }

        GuideGhostItem ghostItem = new GuideGhostItem(buildIngredient(element.items()), 0, 0);

        ItemStack stack = ghostItem.getItem(tickCount);
        entity.setItem(stack);

        entity.tickCount = tickCount;

        RenderUtil.renderEntityInInventory(
                graphics,
                leftPos + element.position().xOffset()+100,
                topPos + element.position().yOffset()+100,
                element.extras().scale(),
                entity,
                isVisible(element.extras().pageScrap()),
                new Quaternionf(),
                false,
                0,0,
                true
        );
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideItemElement element) {
        int cx = leftPos + element.position().xOffset() + 100;
        int cy = topPos + element.position().yOffset() + 100;
        int halfSize = (int) (element.extras().scale() * 0.5f);
        return mouseX >= cx - halfSize && mouseX <= cx + halfSize && mouseY >= cy - halfSize * 2 && mouseY <= cy;
    }

    @Override
    public void handleHover(GuideItemElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        if (!isVisible(element.extras().pageScrap())) return;

        GuideGhostItem ghostItem = new GuideGhostItem(buildIngredient(element.items()), 0, 0);
        ItemStack stack = ghostItem.getItem(ElementRenderDispatcher.getTickCount());
        guiGraphics.renderTooltip(Minecraft.getInstance().font,
                stack.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL),
                Optional.empty(),
                mouseX,
                mouseY);
    }
}
