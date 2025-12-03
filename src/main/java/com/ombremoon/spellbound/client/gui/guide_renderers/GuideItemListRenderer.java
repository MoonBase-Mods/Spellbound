package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideItemList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GuideItemListRenderer implements IPageElementRenderer<GuideItemList> {

    @Override
    public void render(GuideItemList element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        Registry<Item> itemRegistry = Minecraft.getInstance().level.registryAccess().registry(Registries.ITEM).get();
        RandomSource rand = Minecraft.getInstance().level.getRandom();
        rand.setSeed(Math.floorDiv(Minecraft.getInstance().player.tickCount, 10));
        for (int i = 0; i < element.items().size(); i++) {
            GuideItemList.ItemListEntry entry = element.items().get(i);

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

            ItemStack item = isVisible(element.extras().pageScrap()) ? itemRegistry.get(entry.itemLoc()).getDefaultInstance() : itemRegistry.getRandom(rand).get().value().getDefaultInstance();

            GuideItemRenderer.renderItem(graphics, item, leftPos - 10 + element.position().xOffset() + xOffset, topPos + element.position().yOffset() - 8 + yOffset, 1f);
            graphics.drawString(Minecraft.getInstance().font,
                    Component.literal(String.valueOf(entry.count())).withStyle(isVisible(element.extras().pageScrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                    leftPos - 10 + element.position().xOffset() + xOffset + element.extras().countGap(),
                    topPos + element.position().yOffset() + 6 + yOffset,
                    element.extras().textColour(),
                    element.extras().dropShadow());
        }
    }
}
