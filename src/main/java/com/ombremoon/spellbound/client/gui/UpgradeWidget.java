package com.ombremoon.spellbound.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.tree.SkillNode;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class UpgradeWidget {
    private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");
    private static final ResourceLocation LOCKED = ResourceLocation.withDefaultNamespace("advancements/box_unobtained");
    private static final ResourceLocation UNLOCKED = ResourceLocation.withDefaultNamespace("advancements/box_obtained");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final UpgradeWindow window;
    private final Minecraft minecraft;
    private final SkillNode skillNode;
    private final AbstractSpell spell;
    private final FormattedCharSequence title;
    private final int width;
    private final List<FormattedCharSequence> description;
    private final List<FormattedCharSequence> skillDetails;
    private final List<UpgradeWidget> parents = new ObjectArrayList<>();
    private final List<UpgradeWidget> children = new ObjectArrayList<>();
    private final int x;
    private final int y;
    private final int color;
    private boolean showDetails;

    public UpgradeWidget(UpgradeWindow window, Minecraft minecraft, SkillNode skillNode) {
        this.window = window;
        this.minecraft = minecraft;
        this.skillNode = skillNode;
        Skill skill = skillNode.skill();
        this.title = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(skill.getName(), 163));
        this.x = skill.getX();
        this.y = -skill.getY();
        int j = 29 + minecraft.font.width(this.title);
        this.spell = skill.getSpell().createSpell();
        SpellPath path = spell.spellType().getIdentifiablePath();
        this.color = path.getColor();
        this.skillDetails = this.createSkillTooltip(minecraft.font, skill);
        this.description = this.createSkillDescription(minecraft.font, skill);

        for (FormattedCharSequence formattedcharsequence : this.description) {
            j = Math.max(j, minecraft.font.width(formattedcharsequence));
        }

        this.width = j + 3 + 5;
    }

    public Skill getSkill() {
        return this.skillNode.skill();
    }

    private List<FormattedCharSequence> createSkillDescription(Font font, Skill skill) {
        var component = skill.getDescription().withStyle(Style.EMPTY.withColor(this.color));
        if (!this.skillDetails.isEmpty())
            component.append("\n\n").append(Component.translatable("spellbound.skill_tooltip.more_details"));

        return font.split(component, 120);
    }

    private List<FormattedCharSequence> createSkillTooltip(Font font, Skill skill) {
        List<FormattedCharSequence> list = new ArrayList<>();
        List<Component> tooltips = this.spell.getSkillTooltip(skill);
        for (var tooltip : tooltips) {
            list.addAll(font.split(tooltip, 120));
        }

        return list;
    }

    public void drawConnection(GuiGraphics guiGraphics, int x, int y, boolean dropShadow) {
        if (!this.parents.isEmpty()) {
            for (var parent : parents) {
                int i = y + this.y + 30 + 4;
                int j = x + parent.x + 15;
                int k = x + this.x + 15;
                int l = y + this.y + 15;
                int m = y + parent.y + 15;
                int n = dropShadow ? -16777216 : -1;
                if (dropShadow) {
                    guiGraphics.hLine(k - 1, j, i - 1, n);
                    guiGraphics.hLine(k + 1, j, i + 1, n);
                    guiGraphics.hLine(k - 1, j, i + 1, n);
                    guiGraphics.vLine(j - 1, i - 2, m, n);
                    guiGraphics.vLine(j + 1, i - 2, m, n);
                    guiGraphics.vLine(k, i + 1, l - 1, n);
                    guiGraphics.vLine(k - 1, i + 1, l, n);
                    guiGraphics.vLine(k + 1, i + 1, l, n);
                } else {
                    guiGraphics.vLine(k, i, l, n);
                    guiGraphics.vLine(j, i, m, n);
                    guiGraphics.hLine(k, j, i, n);
                }
            }
        }

        for (var widget : this.children) {
            widget.drawConnection(guiGraphics, x, y, dropShadow);
        }
    }

    public void draw(GuiGraphics guiGraphics, int x, int y) {
        var holder = SpellUtil.getSkills(this.minecraft.player);
        boolean flag2 = holder.hasSkill(getSkill());
        guiGraphics.blit(WorkbenchScreen.TEXTURE, x + this.x, y + this.y, flag2 ? 70 : 39 ,226, 30, 30);
        ResourceLocation sprite = this.skillNode.skill().getTexture();
        guiGraphics.blit(sprite, x + this.x + 3, y + this.y + 3, 0, 0, 24, 24, 24, 24);

        for (var widget : this.children) {
            widget.draw(guiGraphics, x, y);
        }
    }

    public boolean isMouseOver(int xPos, int yPos, double mouseX, double mouseY) {
        int i = xPos + this.x;
        int j = i + 30;
        int k = yPos + this.y;
        int l = k + 30;
        return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
    }

    private void addChild(UpgradeWidget widget) {
        this.children.add(widget);
    }

    public void drawHover(GuiGraphics guiGraphics, int x, int y, float fade, int width, int height) {
        this.showDetails = !this.skillDetails.isEmpty() && Screen.hasShiftDown();
        List<FormattedCharSequence> tooltip = this.getDescription();
        boolean flag = width + x + this.x + this.width + 30 >= this.window.getScreen().width;
        boolean flag1 = 115 - y - this.y - 30 <= 6 + tooltip.size() * 9;
        var holder = SpellUtil.getSkills(this.minecraft.player);
        boolean flag2 = holder.hasSkill(getSkill());
        ResourceLocation box = flag2 ? UNLOCKED : LOCKED;

        int i = this.width;
        if (this.showDetails)
            i += 10;
        RenderSystem.enableBlend();
        int j = y + this.y;
        int k;
        if (flag) {
            k = x + this.x - i + 26 + 6;
        } else {
            k = x + this.x;
        }

        int l = 37 + tooltip.size() * 9;
        if (!tooltip.isEmpty()) {
            if (flag1) {
                guiGraphics.blitSprite(TITLE_BOX_SPRITE, k, j + 26 - l, i, l);
            } else {
                guiGraphics.blitSprite(TITLE_BOX_SPRITE, k, j, i, l);
            }
        }

        guiGraphics.blitSprite(box, 200, 26, 200 - i, 0, k, j, i, 26);
        guiGraphics.blit(WorkbenchScreen.TEXTURE, x + this.x, y + this.y, flag2 ? 70 : 39 ,226, 30, 30);
        if (flag) {
            guiGraphics.drawString(this.minecraft.font, this.title, k + 5, y + this.y + 9, -1);
        } else {
            guiGraphics.drawString(this.minecraft.font, this.title, x + this.x + 32, y + this.y + 9, -1);
        }

        if (flag1) {
            for (int i1 = 0; i1 < tooltip.size(); i1++) {
                guiGraphics.drawString(this.minecraft.font, tooltip.get(i1), k + 5, j + 26 - l + 9 + i1 * 9, -5592406, false);
            }
        } else {
            for (int j1 = 0; j1 < tooltip.size(); j1++) {
                guiGraphics.drawString(this.minecraft.font, tooltip.get(j1), k + 5, y + this.y + 9 + 22 + j1 * 9, -5992406, false);
            }
        }

        ResourceLocation sprite = this.skillNode.skill().getTexture();
        guiGraphics.blit(sprite, x + this.x + 3, y + this.y + 3, 0, 0, 24, 24, 24, 24);
    }

    private List<FormattedCharSequence> getDescription() {
        return this.showDetails ? this.skillDetails : this.description;
    }

    public void attachToParents() {
        if (this.parents.isEmpty() && !this.skillNode.parents().isEmpty()) {
            this.skillNode.parents().forEach(skill -> this.parents.add(this.window.getWidget(skill.skill())));
            for (var node : this.skillNode.parents()) {
                if (node != null) this.parents.forEach(widget -> widget.addChild(this));
            }
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public enum Type {
        UNLOCKED,
        LOCKED
    }
}
