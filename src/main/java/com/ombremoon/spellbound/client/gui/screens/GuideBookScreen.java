package com.ombremoon.spellbound.client.gui.screens;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.client.gui.guide.renderers.init.ElementRenderDispatcher;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.client.gui.guide.elements.IPageElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IClickable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IInteractable;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class GuideBookScreen extends Screen {
    protected static final int WIDTH = 415;
    protected static final int HEIGHT = 287;

    protected static final int PAGE_X_OFFSET = 46;
    protected static final int PAGE_Y_OFFSET = 36;

    protected ResourceLocation bookId;
    protected ResourceLocation bookTexture;
    protected int leftPos;
    protected int topPos;
    protected int currentPage = 0;
    protected int lastPage;
    protected List<GuideBookPage> pages;

    private Pair<Double, Double> mouseDrag = Pair.of(0d, 0d);

    public GuideBookScreen(Component title, ResourceLocation bookId, ResourceLocation bookTexture) {
        super(title);
        this.bookId = bookId;
        this.bookTexture = bookTexture;
    }

    public void setPage(int pageNum) {
        this.currentPage = pageNum;
    }

    public void setPage(ResourceLocation pageId) {
        this.currentPage = GuideBookManager.getPageIndex(pageId);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;
        this.pages = GuideBookManager.getBook(bookId);
        this.lastPage = this.pages.size()-1;
        this.currentPage = minecraft.player.getData(SBData.BOOK_LAST_PAGE).getOrDefault(bookId, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(this.bookTexture, this.leftPos, this.topPos, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        int renderLeft = this.leftPos + PAGE_X_OFFSET;
        int renderTop = this.topPos + PAGE_Y_OFFSET;
        ElementRenderDispatcher.tick();
        checkCornerHover(guiGraphics, mouseX, mouseY);
        checkBookmarkHover(guiGraphics, mouseX, mouseY);

        List<IPageElement> elementsToRender = new ArrayList<>();
        List<IPageElement> elementsAboveAll = new ArrayList<>();

        for (IPageElement element : pages.get(currentPage).elements()) {
            switch (element.getRenderOrder()) {
                case BELOW -> elementsToRender.addFirst(element);
                case ABOVE -> elementsAboveAll.add(element);
                default -> elementsToRender.addLast(element);
            }
        }
        elementsToRender.addAll(elementsAboveAll);

        for (IPageElement element : elementsToRender) {
            ElementRenderDispatcher.renderElement(element, guiGraphics, renderLeft, renderTop, mouseX, mouseY, partialTick);

            if (element instanceof IInteractable interactable
                    && interactable instanceof IHoverable) {

                if (ElementRenderDispatcher.isHovering(element, mouseX, mouseY, renderLeft, renderTop))
                    ElementRenderDispatcher.handleHover(element, guiGraphics, renderLeft, renderTop, mouseX, mouseY, partialTick);
            }
        }

    }

    public void checkBookmarkHover(GuiGraphics graphics, int mouseX, int mouseY) {
        if (mouseY > this.topPos + 245 && mouseY < this.topPos + 274 && mouseX > this.leftPos + 235 && mouseX < this.leftPos + 257){
            graphics.renderTooltip(this.font,
                    Component.translatable("guide.basic.restart"),
                    mouseX, mouseY);
        }
    }

    public void checkCornerHover(GuiGraphics graphics, int mouseX, int mouseY) {
        if (currentPage > 0 && (mouseX >= this.leftPos + 41 && mouseX <= this.leftPos + 56 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243)) {
            graphics.blit(
                    CommonClass.customLocation("textures/gui/books/corner_buttons/" + this.bookId.getPath() + ".png"),
                    this.leftPos + 40, this.topPos+226,
                    0, 0,
                    17, 20,
                    17, 20
            );
        } else if (currentPage < lastPage && mouseX >= this.leftPos + 354 && mouseX <= this.leftPos + 370 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243) {
            graphics.blit(
                    CommonClass.customLocation("textures/gui/books/corner_buttons/" + this.bookId.getPath() + ".png"),
                    this.leftPos + 353, this.topPos+226,
                    0, 0,
                    17, 20,
                    -17, 20
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) setDragging(true);
        if (currentPage > 0 && (mouseX >= this.leftPos + 41 && mouseX <= this.leftPos + 56 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243)) {
            while (currentPage > 0) {
                currentPage--;
                if (pages.get(currentPage).isVisible(minecraft.player)) break;
            }

            playClickSound();
            ElementRenderDispatcher.resetElements();
            return true;
        } else if (currentPage < lastPage && mouseX >= this.leftPos + 354 && mouseX <= this.leftPos + 370 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243) {
            for (int i = currentPage+1; i <= lastPage; i++) {
                if (pages.get(i).isVisible(minecraft.player)) {
                    currentPage = i;
                    break;
                }
            }

            playClickSound();
            ElementRenderDispatcher.resetElements();
            return true;
        }

        if (mouseY > this.topPos + 245 && mouseY < this.topPos + 274 && mouseX > this.leftPos + 235 && mouseX < this.leftPos + 257){
            setPage(0);
            playClickSound();
            return true;
        }

        for (IPageElement element : this.pages.get(currentPage).elements()) {
            if (element instanceof IClickable && ElementRenderDispatcher.isHovering(element, (int) mouseX, (int) mouseY, this.leftPos + PAGE_X_OFFSET, this.topPos + PAGE_Y_OFFSET)) {
                ElementRenderDispatcher.handleClick(element, this, mouseX, mouseY, this.leftPos + PAGE_X_OFFSET, this.topPos + PAGE_Y_OFFSET);
                return true;
            }
        }
        return false;
    }

    protected void playClickSound() {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean flag = super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        this.mouseDrag = Pair.of(dragX, dragY);
        return flag;
    }

    public Pair<Double, Double> getMouseDrag() {
        return mouseDrag;
    }

    @Override
    public void onClose() {
        super.onClose();
        ElementRenderDispatcher.resetElements();
        var lastPage = minecraft.player.getData(SBData.BOOK_LAST_PAGE.get());
        lastPage.put(this.bookId, currentPage);
        minecraft.player.setData(SBData.BOOK_LAST_PAGE, lastPage);
    }
}
