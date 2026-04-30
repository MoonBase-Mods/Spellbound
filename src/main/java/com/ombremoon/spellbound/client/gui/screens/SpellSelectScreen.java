package com.ombremoon.spellbound.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.KeyBinds;
import com.ombremoon.spellbound.client.gui.radial.RadialMenu;
import com.ombremoon.spellbound.client.gui.radial.RadialMenuItem;
import com.ombremoon.spellbound.client.gui.radial.SpellRadialMenuItem;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
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
public class SpellSelectScreen extends Screen {
    private static final int RADIAL_WIDTH = 80;
    private static final int RADIAL_HEIGHT = RADIAL_WIDTH;
    private static final int RADIAL_ITEM_WIDTH = 40;
    private static final int RADIAL_ITEM_HEIGHT = RADIAL_ITEM_WIDTH;
    private static final int BACKGROUND_COLOR = 0x3F000000;
    private static final int BACKGROUND_HOVER_COLOR = 0x3FFFFFFF;
    private final Player player;
    private final SpellHandler handler;
    private final RadialMenu radialMenu;
    private SpellRadialMenuItem[] spellItems = new SpellRadialMenuItem[10];
    private final List<RadialMenuItem> items;
    private float x;
    private float y;

    public SpellSelectScreen() {
        super(Component.literal("TEMP RADIAL SELECTION"));
        this.player = Minecraft.getInstance().player;
        this.handler = SpellUtil.getSpellHandler(this.player);
        this.items = new ObjectArrayList<>();
        this.radialMenu = new RadialMenu(this, this.items, RADIAL_WIDTH - RADIAL_ITEM_WIDTH, RADIAL_WIDTH, BACKGROUND_COLOR, BACKGROUND_HOVER_COLOR) {
            @Override
            public void onClickOutside() {
                close();
            }
        };

        for (int i = 0; i < handler.getEquippedSpells().size(); i++) {
            spellItems[i] = new SpellRadialMenuItem(radialMenu, handler.getEquippedSpells().stream().toList().get(i)) {
                @Override
                public boolean onClick() {
                    if (handler.getSelectedSpell() == this.getSpellType() && !(this.getSpellType().getRootSkill().isRadial()))
                        return false;

                    SpellType<?> spellType = this.getSpellType();
                    AbstractSpell spell = spellType.createSpell();
                    handler.setSelectedSpell(spellType);
                    PayloadHandler.setSpell(spellType);
                    radialMenu.close();
                    var radialSkills = spellType.getSkills().stream().filter(skill -> skill.isRadial() && handler.getSkillHolder().hasSkill(skill)).toList();
                    if (spell instanceof RadialSpell && radialSkills.size() > 1)
                        Minecraft.getInstance().setScreen(new SkillSelectScreen(spellType, radialSkills));

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
            this.items.addAll(Arrays.asList(spellItems).subList(0, handler.getEquippedSpells().size()));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
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
