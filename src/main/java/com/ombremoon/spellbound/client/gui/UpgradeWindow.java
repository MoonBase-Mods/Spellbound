package com.ombremoon.spellbound.client.gui;

import com.google.common.collect.Maps;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.tree.SkillNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class UpgradeWindow {
    private static final ResourceLocation BACKGROUND = CommonClass.customLocation("textures/gui/upgrade_background.png");
    private final Minecraft minecraft;
    private final WorkbenchScreen screen;
    private final SkillNode rootNode;
    private final UpgradeWidget rootWidget;
    private final Map<Skill, UpgradeWidget> widgets = Maps.newLinkedHashMap();
    protected double scrollX;
    protected double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private float fade;
    protected boolean centered;

    public UpgradeWindow(Minecraft minecraft, WorkbenchScreen screen, SkillNode rootNode) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.rootNode = rootNode;
        this.rootWidget = new UpgradeWidget(this, this.minecraft, rootNode);
        this.addWidget(this.rootWidget, rootNode.skill());
    }

    public SkillNode getRootNode() {
        return this.rootNode;
    }

    public void drawContents(GuiGraphics guiGraphics, int x, int y) {
        if (!this.centered) {
            this.scrollX = 60;
            this.scrollY = 80;
            this.centered = true;
        }

        guiGraphics.enableScissor(x, y, x + 149, y + 115);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        guiGraphics.blit(BACKGROUND, i / 2 - 325, j / 2 + 75 - 512, 0, 0, 512, 512, 512, 512);
        this.rootWidget.drawConnection(guiGraphics, i, j, true);
        this.rootWidget.drawConnection(guiGraphics, i, j, false);
        this.rootWidget.draw(guiGraphics, i, j);
        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
    }

    public void drawTooltips(GuiGraphics guiGraphics, int width, int height, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, -200.0F);
        guiGraphics.fill(0, 0, 149, 115, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        if (mouseX > 0 && mouseX < 149 && mouseY > 0 && mouseY < 115) {
            for (var widget : this.widgets.values()) {
                if (widget.isMouseOver(i, j, mouseX, mouseY)) {
                    flag = true;
                    widget.drawHover(guiGraphics, i, j, this.fade, width, height);
                    break;
                }
            }
        }

        guiGraphics.pose().popPose();
        if (flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public void scroll(double dragX, double dragY) {
        if (this.maxX - this.minX > 149) {
            this.scrollX = Mth.clamp(this.scrollX + dragX, 0.0, this.maxX);
        }

        if (this.maxY - this.minY > 115) {
            this.scrollY = Mth.clamp(this.scrollY + dragY, 80, this.maxY);
        }
    }

    public void addUpgrade(SkillNode node) {
        UpgradeWidget widget = new UpgradeWidget(this, this.minecraft, node);
        this.addWidget(widget, node.skill());
    }

    private void addWidget(UpgradeWidget widget, Skill skill) {
        this.widgets.put(skill, widget);
        int i = widget.getX();
        int j = i + 32;
        int k = -widget.getY();
        int l = k + 96;
        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

        for (UpgradeWidget upgradeWidget : this.widgets.values()) {
            upgradeWidget.attachToParents();
        }
    }

    public Collection<UpgradeWidget> widgets() {
        return this.widgets.values();
    }

    @Nullable
    public UpgradeWidget getWidget(Skill skill) {
        return this.widgets.get(skill);
    }

    public WorkbenchScreen getScreen() {
        return this.screen;
    }
}
