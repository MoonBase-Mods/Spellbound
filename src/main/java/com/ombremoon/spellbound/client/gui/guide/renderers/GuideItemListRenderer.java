package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideItemListElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public class GuideItemListRenderer implements IPageElementRenderer<GuideItemListElement> {
    private final RandomSource rand;

    public GuideItemListRenderer() {
        this.rand = RandomSource.create(42L);
    }


    @Override
    public void render(GuideItemListElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        Registry<Item> itemRegistry = Minecraft.getInstance().level.registryAccess().registry(Registries.ITEM).get();

        for (int i = 0; i < element.items().size(); i++) {
            GuideItemListElement.ItemListEntry entry = element.items().get(i);

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

            GuideGhostItem ghostItem = new GuideGhostItem(buildIngredient(entry.items()), xOffset, yOffset);

            RenderUtil.renderItem(graphics,
                    isVisible(element.extras().pageScrap()) ? ghostItem.getItem(tickCount) : itemRegistry.getRandom(rand).get().value().getDefaultInstance(),
                    leftPos - 10 + element.position().xOffset() + xOffset,
                    topPos + element.position().yOffset() - 8 + yOffset, 1f
            );

            graphics.drawString(Minecraft.getInstance().font,
                    Component.literal(String.valueOf(entry.count())).withStyle(isVisible(element.extras().pageScrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                    leftPos - 10 + element.position().xOffset() + xOffset + element.extras().countGap(),
                    topPos + element.position().yOffset() + 6 + yOffset,
                    element.extras().textColour(),
                    element.extras().dropShadow());
        }
    }
}
