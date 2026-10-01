package com.ombremoon.spellbound.client.gui.familiar;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHolder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResonanceStoneScreen extends Screen {
    public static final ResourceLocation BACKGROUND = CommonClass.customLocation("textures/gui/resonance_stone/bg.png");
    public static final ResourceLocation LEVEL_BOX = CommonClass.customLocation("textures/gui/resonance_stone/tier_box.png");
    public static final ResourceLocation ARROW_UP = CommonClass.customLocation("textures/gui/resonance_stone/arrow_up.png");
    public static final ResourceLocation ARROW_UP_SELECTED = CommonClass.customLocation("textures/gui/resonance_stone/arrow_up_selected.png");
    public static final ResourceLocation ARROW_DOWN = CommonClass.customLocation("textures/gui/resonance_stone/arrow_down.png");
    public static final ResourceLocation ARROW_DOWN_SELECTED = CommonClass.customLocation("textures/gui/resonance_stone/arrow_down_selected.png");


    private static final int WIDTH = 256;
    private static final int HEIGHT = 166;
    private static final int LIST_X = 4;
    private static final int LIST_Y = 48;
    private static final Pair<Integer, Integer> UP_POS = Pair.of(71, 23);
    private static final Pair<Integer, Integer> DOWN_POS = Pair.of(93, 23);

    private FamiliarHandler familiarHandler;
    private int leftPos;
    private int topPos;
    private int selectedMastery;
    private FamiliarHolder<?, ?> selectedFamiliar = null;
    private FamiliarList list;
    private FamiliarRenderWidget renderWidget;

    public ResonanceStoneScreen(Component title) {
        super(title);
    }

    public void setFamiliar(FamiliarHolder<?, ?> familiar) {
        this.selectedFamiliar = familiar;
        this.renderWidget.setEntity(familiar);
    }

    public SpellMastery getSelectedMastery() {
        return SpellMastery.values()[this.selectedMastery];
    }

    public FamiliarHandler getFamiliarHandler() {
        return familiarHandler;
    }

    public FamiliarHolder<?, ?> getSelectedFamiliar() {
        return selectedFamiliar;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;
        this.familiarHandler = SpellUtil.getFamiliarHandler(this.minecraft.player);
        this.selectedMastery = SpellMastery.NOVICE.ordinal();
        this.list = new FamiliarList(this.minecraft, this, this.leftPos + LIST_X, this.topPos + LIST_Y);
        if (renderWidget != null) renderWidget.setEntity(null);
        this.renderWidget = new FamiliarRenderWidget(this, minecraft, this.leftPos + 115, this.topPos + 5);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.list.mouseDragged(mouseX, mouseY);

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.list.stopScrolling();

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.list.mouseClicked((int) mouseX, (int) mouseY)) return super.mouseClicked(mouseX, mouseY, button);;
        if (this.renderWidget.onClick((int) mouseX, (int) mouseY)) super.mouseClicked(mouseX, mouseY, button);

        if (hoveringArrow(UP_POS, (int) mouseX, (int) mouseY)) {
            if (SpellMastery.values().length-1 == this.selectedMastery) this.selectedMastery = 0;
            else this.selectedMastery++;

            this.list.setDisplayedFamiliars(getSelectedMastery());
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (hoveringArrow(DOWN_POS, (int) mouseX, (int) mouseY)) {
            if (this.selectedMastery == 0) this.selectedMastery = SpellMastery.values().length-1;
            else this.selectedMastery--;

            this.list.setDisplayedFamiliars(getSelectedMastery());
            return super.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

        guiGraphics.blit(LEVEL_BOX, leftPos + 4, topPos + 23, 0, 0, 66, 20, 66, 20);
        guiGraphics.blit(
                hoveringArrow(UP_POS, mouseX, mouseY) ? ARROW_UP_SELECTED : ARROW_UP,
                leftPos + UP_POS.getFirst(), topPos  + UP_POS.getSecond(),
                0, 0,
                20, 20,
                20, 20
        );
        guiGraphics.blit(
                hoveringArrow(DOWN_POS, mouseX, mouseY) ? ARROW_DOWN_SELECTED : ARROW_DOWN,
                leftPos + DOWN_POS.getFirst(), topPos + DOWN_POS.getSecond(),
                0, 0,
                20, 20,
                20, 20
        );
        guiGraphics.drawString(
                minecraft.font,
                getSelectedMastery().name(),
                leftPos + 8, topPos + 29,
                -1, true
        );

        this.list.draw(guiGraphics, mouseX, mouseY);
        this.renderWidget.draw(guiGraphics, mouseX, mouseY);
    }

    private boolean hoveringArrow(Pair<Integer, Integer> pos, int mouseX, int mouseY) {
        int k = leftPos + pos.getFirst();
        int l = topPos + pos.getSecond();
        int i = k + 20;
        int j = l + 20;
        return mouseX > k && mouseX < i && mouseY > l && mouseY < j;
    }
}
