package com.ombremoon.spellbound.client.gui.screens;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.world.item.GuideBookItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
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
        if (mouseY <= this.topPos + 243 || mouseY >= this.topPos + 280) return super.mouseClicked(mouseX, mouseY, button);

        Player player = this.getMinecraft().player;
        boolean hasRuin = player.hasData(SBData.RUIN_ACQUIRED.get()) && player.getData(SBData.RUIN_ACQUIRED.get());
        boolean hasSummon = player.hasData(SBData.SUMMON_ACQUIRED.get()) && player.getData(SBData.SUMMON_ACQUIRED.get());
        boolean hasTransfig = player.hasData(SBData.TRANSFIG_ACQUIRED.get()) && player.getData(SBData.TRANSFIG_ACQUIRED.get());
        boolean hasDivine = player.hasData(SBData.DIVINE_ACQUIRED.get()) && player.getData(SBData.DIVINE_ACQUIRED.get());
        boolean hasDeception = player.hasData(SBData.DECEPTION_ACQUIRED.get()) && player.getData(SBData.DECEPTION_ACQUIRED.get());

        if (mouseX > this.leftPos + 54 && mouseX < this.leftPos + 83 && hasTransfig){
            openBook(SBItems.TRANSFIG_BOOK);
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 102 && mouseX < this.leftPos + 131 && hasRuin){
            openBook(SBItems.RUIN_BOOK);
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 150 && mouseX < this.leftPos + 179 && hasSummon){
            openBook(SBItems.SUMMON_BOOK);
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 280 && mouseX < this.leftPos + 308 && hasDeception){
            openBook(SBItems.DECEPTION_BOOK);
            playClickSound();
            return true;
        }
        if (mouseX > this.leftPos + 327 && mouseX < this.leftPos + 356 && hasDivine){
            openBook(SBItems.DIVINE_BOOK);
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

        Player player = this.getMinecraft().player;
        boolean hasRuin = player.hasData(SBData.RUIN_ACQUIRED.get()) && player.getData(SBData.RUIN_ACQUIRED.get());
        boolean hasSummon = player.hasData(SBData.SUMMON_ACQUIRED.get()) && player.getData(SBData.SUMMON_ACQUIRED.get());
        boolean hasTransfig = player.hasData(SBData.TRANSFIG_ACQUIRED.get()) && player.getData(SBData.TRANSFIG_ACQUIRED.get());
        boolean hasDivine = player.hasData(SBData.DIVINE_ACQUIRED.get()) && player.getData(SBData.DIVINE_ACQUIRED.get());
        boolean hasDeception = player.hasData(SBData.DECEPTION_ACQUIRED.get()) && player.getData(SBData.DECEPTION_ACQUIRED.get());

        if (mouseX > this.leftPos + 54 && mouseX < this.leftPos + 83){
            if (hasTransfig) openTooltip(graphics, mouseX, mouseY, SBItems.TRANSFIG_BOOK);
            else lockedTooltip(graphics, mouseX, mouseY);
        } else if (mouseX > this.leftPos + 102 && mouseX < this.leftPos + 131){
            if (hasRuin) openTooltip(graphics, mouseX, mouseY, SBItems.RUIN_BOOK);
            else lockedTooltip(graphics, mouseX, mouseY);
        } else if (mouseX > this.leftPos + 150 && mouseX < this.leftPos + 179){
            if (hasSummon) openTooltip(graphics, mouseX, mouseY, SBItems.SUMMON_BOOK);
            else lockedTooltip(graphics, mouseX, mouseY);
        } else if (mouseX > this.leftPos + 280 && mouseX < this.leftPos + 308){
            if (hasDeception) openTooltip(graphics, mouseX, mouseY, SBItems.DECEPTION_BOOK);
            else lockedTooltip(graphics, mouseX, mouseY);
        } else if (mouseX > this.leftPos + 327 && mouseX < this.leftPos + 356){
            if (hasDivine) openTooltip(graphics, mouseX, mouseY, SBItems.DIVINE_BOOK);
            else lockedTooltip(graphics, mouseX, mouseY);
        }

        super.checkBookmarkHover(graphics, mouseX, mouseY);
    }

    private void lockedTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.renderTooltip(this.font,
                Component.translatable("guide.basic.locked_book"),
                mouseX, mouseY);
    }

    private void openTooltip(GuiGraphics graphics, int mouseX, int mouseY, Supplier<GuideBookItem> item) {
        graphics.renderTooltip(this.font,
                Component.translatable("guide.basic.open_book", item.get().getDescription()),
                mouseX, mouseY);
    }

    public void openBook(Supplier<GuideBookItem> bookItem) {
        ResourceLocation bookId = bookItem.get().getBookId();
        if (GuideBookManager.getBook(bookId) == null) return;
        RenderUtil.openBook(bookId, bookItem.get().getBookTexture());
    }
}
