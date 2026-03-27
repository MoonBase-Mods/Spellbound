package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.client.gui.guide.elements.GuideItemListElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.client.gui.guide.renderers.init.ElementRenderDispatcher;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GuideItemListRenderer implements IPageElementRenderer<GuideItemListElement> {
    private static final int ITEM_SPACE_X = 32;
    private static final int ITEM_SPACE_Y = 20;
    private final RandomSource rand;

    public GuideItemListRenderer() {
        this.rand = RandomSource.create(42L);
    }

    private List<Pair<Integer, Integer>> getItemPositions(GuideItemListElement element) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        int maxColumns = element.extras().maxColumns();
        int columnGap = element.extras().columnGap();
        int itemHeight = 20;

        int xOffset = element.position().xOffset();
        int yOffset = element.position().yOffset();
        int size = element.items().size();

        for (int i = 0; i < size; i++) {
            if (maxColumns <= 0) {
                xOffset += i * (ITEM_SPACE_X + columnGap);
            } else {
                int yIndex = i / maxColumns;
                int xIndex = i % maxColumns;

                int rowStart = yIndex * maxColumns;
                int rowItems = Math.min(maxColumns, size - rowStart);
                int rowWidth = rowItems * ITEM_SPACE_X + (rowItems - 1) * 6;
                int startX = element.position().xOffset() - (rowWidth / 2);

                xOffset = startX + xIndex * (ITEM_SPACE_X + 6);
                yOffset += yIndex * (itemHeight + element.extras().rowGap());
            }
            pairs.add(Pair.of(xOffset, yOffset));
        }

        return pairs;
    }

    public List<Pair<Integer, Integer>> getOffsets(GuideItemListElement element) {
        Object offsetData = getData(element, "position");
        List<Pair<Integer, Integer>> offsets;
        if (offsetData == null) {
            offsets = getItemPositions(element);
            saveData(element, "position", offsets);
        } else offsets = (List<Pair<Integer, Integer>>) offsetData;

        return offsets;
    }

    @Override
    public void render(GuideItemListElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        Registry<Item> itemRegistry = BuiltInRegistries.ITEM;
        List<Pair<Integer, Integer>> offsets = getOffsets(element);

        for (int i = 0; i < element.items().size() && i < offsets.size(); i++) {
            int xOffset = offsets.get(i).getFirst();
            int yOffset = offsets.get(i).getSecond();
            GuideItemListElement.ItemListEntry entry = element.items().get(i);

            GuideGhostItem ghostItem = new GuideGhostItem(buildIngredientFromStack(entry.items()), xOffset, yOffset);

            RenderUtil.renderItem(graphics,
                    isVisible(element.extras().pageScrap()) ? ghostItem.getItem(tickCount) : itemRegistry.getRandom(rand).get().value().getDefaultInstance(),
                    leftPos - 10 + xOffset,
                    topPos - 8 + yOffset, 1f
            );

            String countString;
            if (entry.count().min() == entry.count().max()) countString = String.valueOf(entry.count().min());
            else countString = entry.count().min() + "-" + entry.count().max();

            graphics.drawString(Minecraft.getInstance().font,
                    Component.literal(countString).withStyle(isVisible(element.extras().pageScrap()) && isVisible(entry.scrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                    leftPos - 10 + xOffset + element.extras().countGap(),
                    topPos + 6 + yOffset,
                    element.extras().textColour(),
                    element.extras().dropShadow());

        }
    }

    @Override
    public void handleHover(GuideItemListElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        List<Pair<Integer, Integer>> offsets = getOffsets(element);

        for (int i = 0; i < offsets.size(); i++) {
            int x = leftPos  + offsets.get(i).getFirst();
            int y = topPos  + offsets.get(i).getSecond();

            if (mouseX >= x && mouseX <= x + ITEM_SPACE_X  && mouseY >= y && mouseY <= y + ITEM_SPACE_Y) {
                GuideGhostItem ghostItem = new GuideGhostItem(buildIngredientFromStack(element.items().get(i).items()), 0, 0);
                guiGraphics.renderTooltip(
                        Minecraft.getInstance().font,
                        ghostItem.getItem(ElementRenderDispatcher.getTickCount()),
                        mouseX, mouseY
                );
            }
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideItemListElement element) {
        return true;
    }
}
