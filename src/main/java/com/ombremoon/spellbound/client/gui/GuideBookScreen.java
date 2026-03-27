package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.client.gui.guide.renderers.init.ElementRenderDispatcher;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.client.gui.guide.elements.IPageElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IClickable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IInteractable;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

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


        for (IPageElement element : pages.get(currentPage).elements()) {
            ElementRenderDispatcher.renderElement(element, guiGraphics, renderLeft, renderTop, mouseX, mouseY, partialTick);

            if (element instanceof IInteractable interactable
                    && interactable instanceof IHoverable) {

                if (ElementRenderDispatcher.isHovering(element, mouseX, mouseY, renderLeft, renderTop))
                    ElementRenderDispatcher.handleHover(element, guiGraphics, renderLeft, renderTop, mouseX, mouseY, partialTick);
            }
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
        if (currentPage > 0 && (mouseX >= this.leftPos + 41 && mouseX <= this.leftPos + 56 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243)) {
            while (currentPage > 0) {
                currentPage--;
                if (pages.get(currentPage).isVisible(minecraft.player)) break;
            }

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ElementRenderDispatcher.resetElements();
            return true;
        } else if (currentPage < lastPage && mouseX >= this.leftPos + 354 && mouseX <= this.leftPos + 370 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243) {
            for (int i = currentPage+1; i <= lastPage; i++) {
                if (pages.get(i).isVisible(minecraft.player)) {
                    currentPage = i;
                    break;
                }
            }

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ElementRenderDispatcher.resetElements();
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
}
