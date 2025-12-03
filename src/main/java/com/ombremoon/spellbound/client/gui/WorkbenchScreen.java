package com.ombremoon.spellbound.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.tree.SkillNode;
import com.ombremoon.spellbound.common.magic.tree.UpgradeTree;
import com.ombremoon.spellbound.main.ConfigHandler;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.RenderUtil;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WorkbenchScreen extends Screen {
    public static final ResourceLocation TEXTURE = CommonClass.customLocation("textures/gui/arcane_gui.png");
    private static final ResourceLocation PATH = CommonClass.customLocation("textures/gui/arcane_gui_text.png");
    private static final ResourceLocation EQUIP_TAB = CommonClass.customLocation("textures/gui/equip_tab.png");
    private static final ResourceLocation EQUIP_PAGE = CommonClass.customLocation("textures/gui/spell_equip.png");
    private static final ResourceLocation CONFIRM_PAGE = CommonClass.customLocation("textures/gui/upgrade_confirm.png");
    private static final int WIDTH = 256;
    private static final int HEIGHT = 166;
    private int selectedIndex = -1;
    private Player player;
    private SpellHandler spellHandler;
    private SkillHolder skillHolder;
    private List<SpellType<?>> spellList;
    private SpellPath spellPath;
    private List<SpellType<?>> equippedSpellList;
    private UpgradeTree upgradeTree;
    private final Map<SpellType<?>, UpgradeWindow> spellTrees = Maps.newLinkedHashMap();
    private SpellType<?> selectedSpell;
    private UpgradeWindow selectedTree;
    private int pageIndex;
    private boolean scrolling;
    private boolean scrollingWindow;
    private float scrollOffs;
    private float windowOffs;
    private int scrollIndex;
    private int windowIndex;
    private int leftPos;
    private int topPos;

    public WorkbenchScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;
        this.player = this.minecraft.player;
        this.upgradeTree = this.player.getData(SBData.UPGRADE_TREE);
        this.spellHandler = SpellUtil.getSpellHandler(this.player);
        this.skillHolder = SpellUtil.getSkills(this.player);
        this.spellList = this.spellHandler.getSpellList().stream().filter(spellType -> spellType.getPath().ordinal() == pageIndex).toList();
        this.equippedSpellList = this.spellHandler.getEquippedSpells().stream().toList();
        this.spellPath = SpellPath.RUIN;
        this.initSpellTrees();
        if (!this.spellList.isEmpty()) {
            this.selectedSpell = this.spellList.get(0);
            this.selectedTree = this.spellTrees.get(this.selectedSpell);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < 4; i++) {
            if (isHovering(19, 26 + (i * 28), 73, 28, mouseX, mouseY) && i != selectedIndex) {
                this.selectedIndex = i;
                if (this.spellList.size() > i + scrollIndex) {
                    this.selectedSpell = this.spellList.get(i + scrollIndex);
                    if (this.pageIndex != -1) {
                        if (this.selectedTree != null) this.selectedTree.centered = false;
                        this.selectedTree = this.spellTrees.get(this.selectedSpell);
                    } else {
                        this.handleSpellSwap(this.selectedSpell);
                    }
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        for (int i = 0; i < 5; i++) {
            if (isHovering(92 + (i * 30), -30, 30, 30, mouseX, mouseY) && i != pageIndex) {
                this.pageIndex = i;
                this.spellPath = SpellPath.values()[this.pageIndex];
                this.selectedIndex = -1;
                this.scrollOffs = 0;
                this.scrollIndex = 0;
                this.windowIndex = 0;
                this.spellList = this.spellHandler.getSpellList().stream().filter(spellType -> spellType.getPath().ordinal() == pageIndex).toList();
                this.selectedSpell = this.spellList.isEmpty() ? null : this.spellList.get(0);
                this.selectedTree = this.selectedSpell != null ? this.spellTrees.get(this.selectedSpell) : null;
                if (this.selectedTree != null) this.selectedTree.centered = false;
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        if (isHovering(0, -26, 30, 28, mouseX, mouseY) && this.pageIndex != -1) {
            this.pageIndex = -1;
            this.selectedIndex = -1;
            this.scrollOffs = 0;
            this.scrollIndex = 0;
            this.windowIndex = 0;
            this.spellList = this.spellHandler.getSpellList().stream().filter(spellType -> !this.spellHandler.getEquippedSpells().contains(spellType)).toList();
            this.selectedSpell = this.spellList.isEmpty() ? null : this.spellList.get(0);
            this.selectedTree = null;
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        if (this.pageIndex == -1) {
            for (int i = 0; i < 4; i++) {
                if (isHovering(95, 13 + (i * 32), 137, 32, mouseX, mouseY)) {
                    if (this.equippedSpellList.size() > i + windowIndex) {
                        SpellType<?> spellType = this.equippedSpellList.get(i + windowIndex);
                        if (spellType != null) {
                            this.spellHandler.unequipSpell(spellType);
                            this.selectedIndex = -1;
                            this.spellList = this.spellHandler.getSpellList().stream().filter(spell -> !this.spellHandler.getEquippedSpells().contains(spell)).toList();
                            this.equippedSpellList = this.spellHandler.getEquippedSpells().stream().toList();
                            PayloadHandler.equipSpell(spellType, false);
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                }
            }
        }

        if (this.selectedTree != null) {
            var widgets = this.selectedTree.widgets();
            int i = Mth.floor(this.selectedTree.scrollX);
            int j = Mth.floor(this.selectedTree.scrollY);
            if (isHovering(98, 16, 149, 115, mouseX, mouseY)) {
                for (var widget : widgets) {
                    if (widget.isMouseOver(i, j, mouseX - this.leftPos - 98, mouseY - this.topPos - 18)) {
                        Skill skill = widget.getSkill();
                        if (this.skillHolder.canUnlockSkill(skill)) {
                            this.skillHolder.unlockSkill(skill, true);
                            PayloadHandler.unlockSkill(skill);
                            return true;
                        }
                    }
                }
            }
        }
        if (isHovering(5, 29 + (scrollIndex * 15), 12, 104, mouseX, mouseY)) {
            this.scrolling = true;
            return true;
        }
        if (isHovering(98, 16, 149, 115, mouseX, mouseY)) {
            this.scrollingWindow = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            int i = this.topPos + 29;
            int j = i + 89;
            this.scrollOffs = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.scrollSpells(this.scrollOffs);
            this.selectedIndex = -1;
            return true;
        } else if (this.scrollingWindow && this.pageIndex == -1) {
            int i = this.topPos + 18;
            int j = i + 88;
            this.windowOffs = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.windowOffs = Mth.clamp(this.windowOffs, 0.0F, 1.0F);
            this.scrollEquippedSpells(this.windowOffs);
            this.selectedIndex = -1;
            return true;
        } else if (this.selectedTree != null && this.scrollingWindow) {
            this.selectedTree.scroll(dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        this.scrollingWindow = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isHovering(5, 29, 88, 109, mouseX, mouseY)) {
            this.scrollOffs = this.subtractInputFromScroll(this.scrollOffs, scrollY, false);
            this.scrollSpells(this.scrollOffs);
            return true;
        } else if (isHovering(96, 15, 149, 115, mouseX, mouseY)) {
            this.windowOffs = this.subtractInputFromScroll(this.windowOffs, scrollY, true);
            this.scrollEquippedSpells(this.windowOffs);
            return true;
        } else if (this.selectedTree != null && isHovering(98, 16, 149, 115, mouseX, mouseY)) {
            if (Screen.hasShiftDown()) {
                this.selectedTree.scroll(scrollY * 16, scrollX * 16);
            } else {
                this.selectedTree.scroll(scrollX * 16, scrollY * 16);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderWindow(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY);
        this.renderBackground(guiGraphics, this.player, this.leftPos, this.topPos, mouseX, mouseY, partialTick);
        this.renderTooltips(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY);
    }

    private void renderBackground(GuiGraphics guiGraphics, Player player, int xPos, int yPos, int mouseX, int mouseY, float partialTick) {
        int textColor = 16777215;
        guiGraphics.blit(TEXTURE, xPos, yPos, 0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < 4; i++) {
            guiGraphics.blit(TEXTURE, xPos + 19, yPos + 27 + (i * 28), 110, 230, 73, 26);
            if (this.spellList.size() > i + scrollIndex) {
                var spellType = this.spellList.get(i + scrollIndex);
                if (spellType != null) {
                    if (isHovering(19, 25 + (i * 26), 73, 26, mouseX, mouseY) || i == selectedIndex) {
                        guiGraphics.blit(TEXTURE, xPos + 19, yPos + 27 + (i * 28), 183, 230, 73, 26);
                    } else {
                        guiGraphics.blit(TEXTURE, xPos + 19, yPos + 27 + (i * 28), 183, 204, 73, 26);
                    }
                    Component component = spellType.createSpell().getName();
                    int height = component.getString().length() > 12 ? 35 : 40;
                    RenderUtil.drawWordWrap(guiGraphics, this.font, component, xPos + 55, yPos + height + (i * 28), 68, textColor);
                }
            }
        }

        int scrollTexture = isHovering(5, 29, 12, 104, mouseX, mouseY) ? 12 : 0;
        guiGraphics.drawCenteredString(this.font, this.selectedSpell != null ? this.selectedSpell.createSpell().getName() : Component.empty(), xPos + 75, yPos + 148, textColor);
        guiGraphics.drawCenteredString(this.font, this.selectedSpell != null ? Component.literal(String.valueOf(skillHolder.getSkillPoints(selectedSpell))) : Component.empty(), xPos + 14, yPos + 149, textColor);
        guiGraphics.blit(TEXTURE, xPos + 5, (int) (yPos + 29 + (89 * scrollOffs)), scrollTexture, 172, 12, 15);
        guiGraphics.blit(PATH, xPos + 4, yPos + 4, 0, 1 + (this.spellPath.ordinal() * 20), 84, 18, 84, 100);

        renderScaledXPBars(guiGraphics, skillHolder, xPos, yPos, textColor);
        renderTabs(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY);
    }

    private void renderWindow(GuiGraphics guiGraphics, int xPos, int yPos, int mouseX, int mouseY) {
        UpgradeWindow window = this.selectedTree;
        if (window == null && this.pageIndex != -1) {
            guiGraphics.fill(xPos + 98, yPos + 16, xPos + 98 + 149, yPos + 16 + 115, -16777216);
        } else if (this.pageIndex == -1) {
            renderSpellSelectPage(guiGraphics, xPos + 95, yPos + 13, mouseX, mouseY);
        } else {
            window.drawContents(guiGraphics, xPos + 98, yPos + 16);
        }
    }

    private void renderSpellSelectPage(GuiGraphics guiGraphics, int xPos, int yPos, int mouseX, int mouseY) {
        guiGraphics.blit(EQUIP_PAGE, xPos, yPos, 0, 0, 155, 121);

        for (int i = 0; i < 4; i++) {
            if (!this.equippedSpellList.isEmpty() && this.equippedSpellList.size() > i + windowIndex) {
                var spellType = this.equippedSpellList.get(i + windowIndex);
                if (spellType != null) {
                    int spellLevel = this.skillHolder.getSpellLevel(spellType);
                    int confirm = isHovering(95, 13 + (i * 29), 137, 30, mouseX, mouseY) ? 184 : 154;
                    guiGraphics.blit(EQUIP_PAGE, xPos + 2, yPos + 2 + (i * 29), 0,confirm, 135, 30);
                    guiGraphics.blit(spellType.getTexture(), xPos + 5, yPos + 5 + (i * 29), 0, 0, 24, 24, 24, 24);
                    Component component = spellType.createSpell().getName();
                    guiGraphics.drawString(this.font, component, xPos + 33, yPos + 6 + (i * 29), -1);
                    guiGraphics.drawString(this.font, Component.literal("Lvl " + spellLevel).withStyle(spellLevel == 5 ? ChatFormatting.YELLOW : ChatFormatting.WHITE), xPos + 33, yPos + 19 + (i * 29), -1);
                }
            } else {
                guiGraphics.blit(EQUIP_PAGE, xPos + 2, yPos + 2 + (i * 29), 0, 124, 135, 30);
            }
        }

        int scrollTexture = isHovering(233, 18, 12, 113, mouseX, mouseY) ? 167 : 155;
        guiGraphics.blit(EQUIP_PAGE, xPos + 138, (int) (yPos + 5 + (96 * windowOffs)), scrollTexture, 0, 12, 15);
    }

    private void renderTooltips(GuiGraphics guiGraphics, int xPos, int yPos, int mouseX, int mouseY) {
        if (this.selectedTree != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float)(xPos + 98), (float)(yPos + 16), 400);
            RenderSystem.enableDepthTest();
            this.selectedTree.drawTooltips(guiGraphics, xPos, yPos, mouseX - xPos - 98, mouseY - yPos - 16);
            RenderSystem.disableDepthTest();
            guiGraphics.pose().popPose();
        }

        if (isHovering(4, 142, 20, 20, mouseX, mouseY)) {
            guiGraphics.renderTooltip(minecraft.font, Component.literal("The amount of skill points for the selected spell."), mouseX, mouseY);
        }

        if (isHovering(101, 5, 144, 5, mouseX, mouseY)) {
            int level = this.skillHolder.getPathLevel(this.spellPath);
            Component component = Component.translatable("spellbound.path." + this.spellPath.getSerializedName()).append(" ").append(Component.translatable("spellbound.path.level")).append(": " + level);
            guiGraphics.renderTooltip(minecraft.font, component, mouseX, mouseY);
        }
    }

    private void renderTabs(GuiGraphics guiGraphics, int xPos, int yPos, int mouseX, int mouseY) {
        for (int i = 0; i < 5; i++) {
            guiGraphics.blit(TEXTURE, xPos + 92 + (i * 30), yPos - 26, 0, 198, 30, 26);
            guiGraphics.blit(CommonClass.customLocation("textures/gui/paths/" + SpellPath.values()[i].name().toLowerCase(Locale.ROOT) + ".png"), xPos + 95 + (i * 30), yPos - 23, 0, 0, 24, 24, 24, 24);
            if (isHovering(92 + (i * 30), -30, 30, 26, mouseX, mouseY) || i == pageIndex) {
                guiGraphics.blit(TEXTURE, xPos + 92 + (i * 30), yPos - 28, 0, 224, 30, 32);
                guiGraphics.blit(CommonClass.customLocation("textures/gui/paths/" + SpellPath.values()[i].name().toLowerCase(Locale.ROOT) + ".png"), xPos + 95 + (i * 30), yPos - 25, 0, 0, 24, 24, 24, 24);
            }
        }

        boolean flag = isHovering(3, -26, 30, 28, mouseX, mouseY) || this.pageIndex == -1;
        int tabY = flag ? 29 : 26;
        int bookY = flag ? 26 : 23;
        int uOffset = flag ? 39 : 69;
        int vHeight = flag ? 33 : 28;
        guiGraphics.blit(TEXTURE, xPos, yPos - tabY, uOffset, 192, 30, vHeight);
        guiGraphics.blit(EQUIP_TAB, xPos + 3, yPos - bookY, 0, 0, 24, 24, 24, 24);
    }

    private void renderScaledXPBars(GuiGraphics guiGraphics, SkillHolder skillHolder, int xPos, int yPos, int textColor) {
        float pathXP = skillHolder.getPathXp(this.spellPath);
        int pathLevel = skillHolder.getPathLevel(this.spellPath);
        if (pathXP > 0) {
            int xpGoal = skillHolder.getXPGoal(pathLevel + 1);
            int scale = RenderUtil.getScaledRender(pathXP - (100 * pathLevel), xpGoal - (100 * pathLevel), 141);
            guiGraphics.blit(TEXTURE, xPos + 101, yPos + 5, 115, 188, scale, 5);
        }

        if (this.selectedSpell == null) return;

        float spellXP = skillHolder.getSpellXp(this.selectedSpell);
        int spellLevel = skillHolder.getSpellLevel(this.selectedSpell);
        if (spellXP > 0) {
            int prevXpGoal = spellLevel > 0 ? skillHolder.getXPGoal(spellLevel) : 0;
            int currentXP = (int) (spellXP - prevXpGoal);
            int xpGoal = skillHolder.getXPGoal(spellLevel + 1) - prevXpGoal;
            int scale = RenderUtil.getScaledRender(currentXP, xpGoal, 122);
            guiGraphics.drawCenteredString(this.font, Component.literal("Lvl " + spellLevel + " - " + currentXP + " / " + xpGoal), xPos + 190, yPos + 143, textColor);
            guiGraphics.blit(TEXTURE, xPos + 129, yPos + 153, 134, 194, spellXP != skillHolder.getXPGoal(SkillHolder.MAX_SPELL_LEVEL) ? scale : 122, 5);
        }
    }

    private void initSpellTrees() {
        for (var root : this.upgradeTree.roots()) {
            UpgradeWindow window = new UpgradeWindow(this.minecraft, this, root);
            this.spellTrees.put(root.skill().getSpell(), window);
        }

        for (var child : this.upgradeTree.children()) {
            UpgradeWindow window = this.getWindow(child);
            if (window != null) {
                window.addUpgrade(child);
            }
        }
    }

    private void handleSpellSwap(SpellType<?> spellType) {
        if (this.spellHandler.getEquippedSpells().size() < ConfigHandler.COMMON.maxSpellListSize.get()) {
            this.spellHandler.equipSpell(spellType);
            this.spellList = this.spellHandler.getSpellList().stream().filter(spell -> !this.spellHandler.getEquippedSpells().contains(spell)).toList();
            this.equippedSpellList = this.spellHandler.getEquippedSpells().stream().toList();
            this.selectedIndex = -1;
            PayloadHandler.equipSpell(spellType, true);
        }
    }

    @Nullable
    private UpgradeWindow getWindow(SkillNode skillNode) {
        return this.spellTrees.get(skillNode.skill().getSpell());
    }

    private boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        double d0 = pMouseX - (double)(i + pX);
        double d1 = pMouseY - (double)(j + pY);
        return d0 >= 0 && d1 >= 0 && d0 < pWidth && d1 < pHeight;
    }

    private float subtractInputFromScroll(float scrollOffs, double input, boolean equipTab) {
        int size = equipTab ? this.equippedSpellList.size() : this.spellList.size();
        return Mth.clamp(scrollOffs - (float)(input / size), 0.0F, 1.0F);
    }

    private void scrollSpells(float pos) {
        int k = Math.max(0, this.spellList.size() - 4);
        for (int l = 0; l <= k; l++) {
            float thresh = (float) l / (k + 1);
            float nextThresh = (float) (l + 1) / (k + 1);
            if (pos > thresh && pos <= nextThresh) {
                if (this.selectedIndex == 3 && this.scrollIndex > l || this.selectedIndex == 0 && this.scrollIndex < l) {
                    this.selectedIndex = -1;
                } else if (this.scrollIndex < l) {
                    this.selectedIndex--;
                } else if (this.scrollIndex > l) {
                    this.selectedIndex++;
                }
                this.scrollIndex = l;
            }
        }
    }

    private void scrollEquippedSpells(float pos) {
        int k = Math.max(0, this.equippedSpellList.size() - 4);
        for (int l = 0; l <= k; l++) {
            float thresh = (float) l / (k + 1);
            float nextThresh = (float) (l + 1) / (k + 1);
            if (pos > thresh && pos <= nextThresh)
                this.windowIndex = l;
        }
    }
}
