package com.ombremoon.spellbound.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.KeyBinds;
import com.ombremoon.spellbound.client.gui.radial.RadialMenu;
import com.ombremoon.spellbound.client.gui.radial.RadialMenuItem;
import com.ombremoon.spellbound.client.gui.radial.SkillRadialMenuItem;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkillSelectScreen extends Screen {
    private static final int RADIAL_WIDTH = 80;
    private static final int RADIAL_HEIGHT = RADIAL_WIDTH;
    private static final int RADIAL_ITEM_WIDTH = 40;
    private static final int RADIAL_ITEM_HEIGHT = RADIAL_ITEM_WIDTH;
    private static final int BACKGROUND_COLOR = 0x3F000000;
    private static final int BACKGROUND_HOVER_COLOR = 0x3FFFFFFF;
    private final Player player;
    private final SpellType<?> spellType;
    private final List<Skill> radialSkills;
    private final SkillHolder skills;
    private final RadialMenu radialMenu;
    private SkillRadialMenuItem[] skillItems = new SkillRadialMenuItem[5];
    private final List<RadialMenuItem> items;
    private float x;
    private float y;

    protected SkillSelectScreen(SpellType<?> spellType, List<Skill> radialSkills) {
        super(Component.literal("TEMP SKILL RADIAL SELECTION"));
        this.player = Minecraft.getInstance().player;
        this.spellType = spellType;
        this.radialSkills = radialSkills;
        this.skills = SpellUtil.getSkills(this.player);
        this.items = new ObjectArrayList<>();
        this.radialMenu = new RadialMenu(this, this.items, RADIAL_WIDTH - RADIAL_ITEM_WIDTH, RADIAL_WIDTH, BACKGROUND_COLOR, BACKGROUND_HOVER_COLOR) {
            @Override
            public void onClickOutside() {
                close();
            }
        };

        for (int i = 0; i < radialSkills.size(); i++) {
            skillItems[i] = new SkillRadialMenuItem(radialMenu, this.spellType, radialSkills.get(i)) {
                @Override
                public boolean onClick() {
                    Skill choice = this.getSkill();
                    if (skills.getChoice(spellType) == choice)
                        return false;

                    skills.setChoice(spellType, choice);
                    PayloadHandler.updateChoice(spellType, choice);
                    radialMenu.close();

                    return true;
                }
            };
        }
    }

    @Override
    protected void init() {
        super.init();
        this.x = (float) this.width / 2;
        this.y = (float) this.height / 2;
        if (this.items.isEmpty()) {
            this.items.addAll(Arrays.asList(skillItems).subList(0, this.radialSkills.size()));
        }
    }

    @Override
    public void tick() {
        super.tick();

        radialMenu.tick();

        if (radialMenu.isClosed())
            Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        radialMenu.clickItem();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyBinds.SELECT_SPELL_BINDING.getKey().getValue()) {
            radialMenu.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        poseStack.popPose();
        radialMenu.draw(guiGraphics, partialTick, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
