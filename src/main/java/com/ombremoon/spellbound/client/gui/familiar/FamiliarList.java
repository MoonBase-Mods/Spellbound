package com.ombremoon.spellbound.client.gui.familiar;

import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class FamiliarList {
    public static final ResourceLocation SCROLLER = CommonClass.customLocation("textures/gui/resonance_stone/scroller.png");
    public static final ResourceLocation SCROLLER_SELECTED = CommonClass.customLocation("textures/gui/resonance_stone/scroller_selected.png");
    private static final int SCROLLER_WIDTH = 11;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int MAX_DISPLAYED = 5;

    public final Minecraft minecraft;
    public final ResonanceStoneScreen screen;

    private int x;
    private int y;
    private boolean scrolling;
    private int scrollIndex = 0;
    private int maxScroll = 0;
    private List<FamiliarWidget> widgets;
    private double lastMouseY = 0d;

    public FamiliarList(Minecraft minecraft, ResonanceStoneScreen screen, int x, int y) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.widgets = new ArrayList<>();
        setDisplayedFamiliars(SpellMastery.NOVICE);
        this.x = x;
        this.y = y;
    }

    public void setDisplayedFamiliars(SpellMastery mastery) {
        this.scrollIndex = 0;
        boolean locked = this.screen.getFamiliarHandler().getSpellHandler().getSkillHolder().getMastery(SpellPath.SUMMONS).ordinal() < mastery.ordinal();
        this.widgets = new ArrayList<>();
        for (var familiar : FamiliarHandler.getMasterySortedFamiliars(mastery)) {
            this.widgets.add(new FamiliarWidget(this, familiar, locked));
        }

        this.maxScroll = this.widgets.size() - MAX_DISPLAYED;
    }

    public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.blit(
                scrolling || isHoveringScroller(mouseX, mouseY) ? SCROLLER_SELECTED : SCROLLER,
                this.x + 1, getScrollerYPos(),
                0, 0,
                SCROLLER_WIDTH, SCROLLER_HEIGHT,
                SCROLLER_WIDTH, SCROLLER_HEIGHT
        );

        int j = 0;
        for (int i = scrollIndex; j < MAX_DISPLAYED && i < widgets.size(); i++) {
            this.widgets.get(i).drawButton(graphics, this.x+15, this.y + (j*22), mouseX, mouseY); //22 is from 19 height + 3 spacing
            j++;
        }
    }

    private int getScrollerYPos() {
        int scrollerY = this.y;
        if (maxScroll != 0) scrollerY += (int) Math.floor(94d * ((double) scrollIndex/ (double) (maxScroll+1)));

        return scrollerY;
    }

    public boolean isHoveringScroller(int mouseX, int mouseY) {
        int i = getScrollerYPos();
        int j = i + SCROLLER_HEIGHT;
        int k = this.x + SCROLLER_WIDTH;
        return mouseX > this.x && mouseX < k && mouseY > i && mouseY < j;
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (isHoveringScroller(mouseX, mouseY)) {
            this.scrolling = true;
            return true;
        }

        int j = 0;
        for (int i = scrollIndex; j < MAX_DISPLAYED && i < widgets.size(); i++) {
            if (this.widgets.get(i).isHovering(x + 15, y + (j*22), mouseX, mouseY)) {
                this.widgets.get(i).onClick();
                return true;
            }
            j++;
        }

        return false;
    }

    public void mouseDragged(double mouseX, double mouseY) {
        if (!this.scrolling) return;

        int i = getScrollerYPos();
        int j = i + SCROLLER_HEIGHT;
        if (mouseY < i && mouseY < lastMouseY && scrollIndex > 0) {
            scrollIndex--;
        } else if (mouseY > j && mouseY > lastMouseY && scrollIndex <= maxScroll) {
            scrollIndex++;
        }
        lastMouseY = mouseY;
    }

    public void stopScrolling() {
        this.scrolling = false;
    }
}
