package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.GuideItemListElement;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;

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

            GuideGhostItem ghostItem = new GuideGhostItem(buildIngredientFromStack(entry.items()), xOffset, yOffset);

            RenderUtil.renderItem(graphics,
                    isVisible(element.extras().pageScrap()) ? ghostItem.getItem(tickCount) : itemRegistry.getRandom(rand).get().value().getDefaultInstance(),
                    leftPos - 10 + element.position().xOffset() + xOffset,
                    topPos + element.position().yOffset() - 8 + yOffset, 1f
            );

            String countString;
            if (entry.count() instanceof ConstantValue(float value)) countString = String.valueOf((int) value);
            else if (entry.count() instanceof UniformGenerator(ConstantValue min, ConstantValue max)) countString = (int) min.value() + "-" + (int) max.value();
            else countString = "1";

            graphics.drawString(Minecraft.getInstance().font,
                    Component.literal(countString).withStyle(isVisible(element.extras().pageScrap()) && isVisible(entry.scrap()) ? ChatFormatting.RESET : ChatFormatting.OBFUSCATED),
                    leftPos - 10 + element.position().xOffset() + xOffset + element.extras().countGap(),
                    topPos + element.position().yOffset() + 6 + yOffset,
                    element.extras().textColour(),
                    element.extras().dropShadow());
        }
    }

    @Override
    public void handleHover(GuideItemListElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        int y = mouseY - topPos - element.position().yOffset();
        int index;
        if (element.extras().maxRows() > 0) {
            index = Math.floorDiv(y, element.extras().rowGap());
            int x = mouseX - leftPos - element.position().xOffset();
            index += Math.floorDiv(x, element.extras().columnGap());
        } else {
            index = Math.floorDiv(y, 20);
        }

        index = Math.clamp(index, 0, element.items().size()-1);

        GuideItemListElement.ItemListEntry entry = element.items().get(index);
        ItemStack stack = entry.items().isEmpty() ? ItemStack.EMPTY : entry.items().get((int) (Math.floor(ElementRenderDispatcher.getTickCount() / 30f) % entry.items().size()));

        guiGraphics.renderTooltip(Minecraft.getInstance().font,
                stack.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL),
                Optional.empty(),
                mouseX,
                mouseY);
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideItemListElement element) {
        if (mouseX < leftPos + element.position().xOffset() || mouseY <= topPos + element.position().yOffset()) return false;

        int listSize = element.items().size();
        int minWidth = 20 + element.extras().countGap() + Minecraft.getInstance().font.width("1-3");
        int length = element.extras().maxRows() <= 0 ? listSize * 20 : Math.min(element.extras().maxRows(), listSize) * element.extras().rowGap();
        int width = element.extras().maxRows() <= 0 ? minWidth : (int) ((minWidth + element.extras().columnGap()) * Math.floorDiv(listSize, element.extras().maxRows()) + 1) - element.extras().columnGap();

        return mouseX <= leftPos + element.position().xOffset() + width && mouseY < topPos + element.position().yOffset() + length;
    }
}
