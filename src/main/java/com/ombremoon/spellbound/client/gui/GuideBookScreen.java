package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.client.gui.guide_renderers.ElementRenderDispatcher;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.IPageElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class GuideBookScreen extends Screen {
    private static final int WIDTH = 415;
    private static final int HEIGHT = 287;

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

        for (IPageElement element : pages.get(currentPage).elements()) {
            ElementRenderDispatcher.renderElement(element, guiGraphics, this.leftPos + 47, this.topPos + 36, mouseX, mouseY, partialTick);
        }
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentPage > 0 && (mouseX >= this.leftPos + 41 && mouseX <= this.leftPos + 56 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243)) {
            currentPage--;
//            while (currentPage > 0) {
//                currentPage--;
//                //if (pages.get(currentPage).isVisible()) break;
//            }

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        } else if (currentPage < lastPage && mouseX >= this.leftPos + 354 && mouseX <= this.leftPos + 370 && mouseY >= this.topPos + 230 && mouseY <= this.topPos + 243) {
//            for (int i = currentPage; i < lastPage; i++) {
//                if (pages.get(i).isVisible()) {
//                    currentPage = i;
//                    break;
//                }
//            }
            currentPage++;

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        return false;
    }
}
