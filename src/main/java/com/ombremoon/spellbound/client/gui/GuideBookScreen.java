package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.client.gui.guide.renderers.ElementRenderDispatcher;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.client.gui.guide.elements.IPageElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IClickable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IInteractable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public class GuideBookScreen extends Screen {
    private static final int WIDTH = 415;
    private static final int HEIGHT = 287;

    private static final int PAGE_X_OFFSET = 46;
    private static final int PAGE_Y_OFFSET = 36;

    private ResourceLocation bookId;
    private ResourceLocation bookTexture;
    private int leftPos;
    private int topPos;
    private int currentPage = 0;
    private int lastPage;
    private List<GuideBookPage> pages;

    public GuideBookScreen(Component title, ResourceLocation bookId, ResourceLocation bookTexture) {
        super(title);
        this.bookId = bookId;
        this.bookTexture = bookTexture;
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

        for (IPageElement element : pages.get(currentPage).elements()) {
            ElementRenderDispatcher.renderElement(element, guiGraphics, renderLeft, renderTop, mouseX, mouseY, partialTick);

            if (element instanceof IInteractable interactable
                    && ElementRenderDispatcher.isHovering(element, mouseX, mouseY, renderLeft, renderTop)) {

                if (interactable instanceof IHoverable)
                    ElementRenderDispatcher.handleHover(element, guiGraphics, renderLeft, renderTop, mouseX, mouseY, partialTick);
            }
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
            ElementRenderDispatcher.resetTickCount();
            return true;
        } else if (currentPage < lastPage && mouseX >= this.leftPos + 354 && mouseX <= this.leftPos + 370 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243) {
            for (int i = currentPage+1; i <= lastPage; i++) {
                if (pages.get(i).isVisible(minecraft.player)) {
                    currentPage = i;
                    break;
                }
            }

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ElementRenderDispatcher.resetTickCount();
            return true;
        }

        for (IPageElement element : this.pages.get(currentPage).elements()) {
            if (element instanceof IClickable && ElementRenderDispatcher.isHovering(element, (int) mouseX, (int) mouseY, this.leftPos + PAGE_X_OFFSET, this.topPos + PAGE_Y_OFFSET)) {
                ElementRenderDispatcher.handleClick(element, this);
                return true;
            }
        }

        return false;
    }
}
