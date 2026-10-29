package com.ombremoon.spellbound.client.gui.screens;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.world.item.GuideBookItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

public class BasicGuideScreen extends GuideBookScreen {

    public BasicGuideScreen(Component title) {
        super(title, CommonClass.customLocation("studies_in_the_arcane"), CommonClass.customLocation("textures/gui/books/studies_in_the_arcane.png"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY <= this.topPos + 243 || mouseY >= this.topPos + 280)
            return super.mouseClicked(mouseX, mouseY, button);

        if (mouseX > this.leftPos + 54 && mouseX < this.leftPos + 83 && openBook(SBItems.TRANSFIG_BOOK)){
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 102 && mouseX < this.leftPos + 131 && openBook(SBItems.RUIN_BOOK)){
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 150 && mouseX < this.leftPos + 179 && openBook(SBItems.SUMMON_BOOK)){
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 280 && mouseX < this.leftPos + 308 && openBook(SBItems.DECEPTION_BOOK)){
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 327 && mouseX < this.leftPos + 356 && openBook(SBItems.DIVINE_BOOK)){
            playClickSound();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void checkBookmarkHover(GuiGraphics graphics, int mouseX, int mouseY) {
        if (mouseY <= this.topPos + 243 || mouseY >= this.topPos + 280) {
            super.checkBookmarkHover(graphics, mouseX, mouseY);
            return;
        }

        if (mouseX > this.leftPos + 54 && mouseX < this.leftPos + 83){
            openTooltip(graphics, mouseX, mouseY, SBItems.TRANSFIG_BOOK);
        } else if (mouseX > this.leftPos + 102 && mouseX < this.leftPos + 131){
            openTooltip(graphics, mouseX, mouseY, SBItems.RUIN_BOOK);
        } else if (mouseX > this.leftPos + 150 && mouseX < this.leftPos + 179){
            openTooltip(graphics, mouseX, mouseY, SBItems.SUMMON_BOOK);
        } else if (mouseX > this.leftPos + 280 && mouseX < this.leftPos + 308){
            openTooltip(graphics, mouseX, mouseY, SBItems.DECEPTION_BOOK);
        } else if (mouseX > this.leftPos + 327 && mouseX < this.leftPos + 356){
            openTooltip(graphics, mouseX, mouseY, SBItems.DIVINE_BOOK);
        }

        super.checkBookmarkHover(graphics, mouseX, mouseY);
    }

    private void openTooltip(GuiGraphics graphics, int mouseX, int mouseY, Supplier<GuideBookItem> item) {
        Player player = Minecraft.getInstance().player;
        GuideBookItem book = item.get();
        if (player.getData(book.getBookAttachment())) {
            graphics.renderTooltip(this.font,
                    Component.translatable("guide.basic.open_book", book.getDescription()),
                    mouseX, mouseY);
        } else {
            graphics.renderTooltip(this.font,
                    Component.translatable("guide.basic.locked_book"),
                    mouseX, mouseY);
        }
    }

    public boolean openBook(Supplier<GuideBookItem> bookItem) {
        Player player = Minecraft.getInstance().player;
        GuideBookItem item = bookItem.get();
        if (!player.getData(item.getBookAttachment())) {
            return false;
        }

        ResourceLocation bookId = item.getBookId();
        if (GuideBookManager.getBook(bookId) == null) {
            return false;
        }

        RenderUtil.openBook(bookId, item.getBookTexture());
        return true;
    }
}
