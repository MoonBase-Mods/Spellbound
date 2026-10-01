package com.ombremoon.spellbound.client.gui.familiar;

import com.ombremoon.spellbound.common.magic.familiars.FamiliarHolder;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FamiliarRenderWidget {
    public static final ResourceLocation EQUIP_BUTTON = CommonClass.customLocation("textures/gui/resonance_stone/equip_button.png");
    public static final ResourceLocation EQUIP_BUTTON_SELECTED = CommonClass.customLocation("textures/gui/resonance_stone/equip_button_selected.png");
    public static final ResourceLocation REBIRTH_BUTTON = CommonClass.customLocation("textures/gui/resonance_stone/rebirth_button.png");
    public static final ResourceLocation REBIRTH_BUTTON_SELECTED = CommonClass.customLocation("textures/gui/resonance_stone/rebirth_button_selected.png");
    public static final ResourceLocation EXP_BAR = CommonClass.customLocation("textures/gui/resonance_stone/exp_bar.png");
    public static final ResourceLocation EXP_BAR_FILLED = CommonClass.customLocation("textures/gui/resonance_stone/exp_bar_filled.png");

    private ResonanceStoneScreen screen;
    private Minecraft minecraft;
    private LivingEntity selectedEntity;
    private int x;
    private int y;
    private List<FamiliarRenderButton> buttons;
    private int[] bondTooltipRegions = new int[]{6, 28, 52, 76, 100, 124};

    public FamiliarRenderWidget(ResonanceStoneScreen screen, Minecraft minecraft, int x, int y) {
        this.screen = screen;
        this.minecraft = minecraft;
        this.x = x;
        this.y = y;
        this.buttons = new ArrayList<>();
        setEntity(screen.getSelectedFamiliar());
    }

    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.buttons = new ArrayList<>();
        guiGraphics.blit(
                EXP_BAR,
                this.x + 6, this.y + 137,
                0, 0,
                124, 9,
                124, 9
        );

        if (this.screen.getSelectedFamiliar() == null || !this.screen.getFamiliarHandler().getUnlockedFamiliars().contains(this.screen.getSelectedFamiliar()) || this.selectedEntity == null) return;

        guiGraphics.drawString(
                minecraft.font,
                this.selectedEntity.getName().copy().withStyle(ChatFormatting.BOLD),
                this.x + 5, this.y + 5,
                -1,
                true
        );
        guiGraphics.drawString(
                minecraft.font,
                this.screen.getSelectedFamiliar().getType().getName(),
                this.x + 5, this.y + 5 + minecraft.font.lineHeight,
                -1, false
        );

        int level = this.screen.getFamiliarHandler().getLevelForFamiliar(this.screen.getSelectedFamiliar());
        int xpProg = 5;
        if (level >= 1) {
            xpProg += 21;
            xpProg += (level - 1) * 24;
        }
        xpProg += (int) (this.screen.getFamiliarHandler().progressToNextLevel(this.screen.getSelectedFamiliar()) * 24);
        guiGraphics.blit(
                EXP_BAR_FILLED,
                this.x + 6, this.y + 137,
                0, 0,
                xpProg, 9,
                124, 9
        );

        int rebirths = this.screen.getFamiliarHandler().getRebirths(this.screen.getSelectedFamiliar());

        buttons.add(new FamiliarRenderButton(this,
                x + 115,
                y + 51,
                EQUIP_BUTTON,
                EQUIP_BUTTON_SELECTED,
                this::selectCallback,
                Component.literal("Equip this familiar.")));
        if (level == this.screen.getSelectedFamiliar().getMaxLevel())
            buttons.add(new FamiliarRenderButton(
                    this,
                    x + 115,
                    y + 68,
                    REBIRTH_BUTTON,
                    REBIRTH_BUTTON_SELECTED,
                    this::rebirthCallback,
                    Component.translatable("spellbound.familiars.tooltip.rebirths", rebirths),
                    Component.empty(),
                    Component.translatable("spellbound.familiars.tooltip.rebirths1"),
                    Component.translatable("spellbound.familiars.tooltip.rebirths2")
                    ));

        for (var button : this.buttons) {
            button.draw(guiGraphics, mouseX, mouseY);
        }

        RenderUtil.renderEntityInInventory(
                guiGraphics,
                this.x + 67, this.y+ 85,
                35f,
                selectedEntity,
                true,
                new Quaternionf(),
                true, mouseX, mouseY);

        int hoverIndex = findHover(mouseX, mouseY);
        if (hoverIndex < 0) return;

        FamiliarAffinity affinity = this.screen.getSelectedFamiliar().getAffinities().get(hoverIndex);
        if (affinity == null) return;
        guiGraphics.renderTooltip(
                minecraft.font,
                List.of(
                        affinity.getName().copy().withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                        Component.empty(),
                        affinity.getDescription()
                ),
                Optional.empty(),
                mouseX, mouseY
        );
    }

    public int findHover(int mouseX, int mouseY) {
        if (mouseX < this.x+6 || mouseX > this.x + 130) return -1;

        for (int i = 0; i < this.bondTooltipRegions.length; i++) {
            int regionStart = this.x + this.bondTooltipRegions[i];
            if (mouseX > regionStart && mouseX < regionStart + 6 && mouseY > this.y + 137 && mouseY < this.y+146)
                return i;
        }

        return -1;
    }

    public boolean onClick(int mouseX, int mouseY) {
        for (var button : this.buttons) {
            if (button.isHovering(mouseX, mouseY)) {
                button.consume(this.screen);
                return true;
            }
        }

        return false;
    }

    private void selectCallback(ResonanceStoneScreen screen) {
        if (screen.getSelectedFamiliar() == null || this.selectedEntity == null) return;

        screen.getFamiliarHandler().selectFamiliar(screen.getSelectedFamiliar());
        PayloadHandler.equipFamiliar(screen.getSelectedFamiliar());
        minecraft.player.sendSystemMessage(Component.translatable("spellbound.familiars.equipped", this.selectedEntity.getName()));
        screen.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void rebirthCallback(ResonanceStoneScreen screen) {
        if (screen.getSelectedFamiliar() == null || this.selectedEntity == null) return;

        screen.getFamiliarHandler().rebirthFamiliar(screen.getSelectedFamiliar());
        PayloadHandler.rebirthFamiliar(screen.getSelectedFamiliar());
        minecraft.player.sendSystemMessage(Component.translatable("spellbound.familiars.rebirthed", this.selectedEntity.getName()));
        screen.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void setEntity(FamiliarHolder<?, ?> familiar) {
        if (this.selectedEntity != null) {
            this.selectedEntity.discard();
            if (familiar == null) return;
        } else if (familiar == null) return;
        this.selectedEntity = familiar.getEntity().create(this.minecraft.level);
    }
}
