package com.ombremoon.spellbound.client.gui.familiar;

import com.ombremoon.spellbound.common.magic.familiars.FamiliarHolder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class FamiliarWidget {
    public static final ResourceLocation LOCKED_BOX = CommonClass.customLocation("textures/gui/resonance_stone/locked_box.png");
    public static final ResourceLocation LOCK = CommonClass.customLocation("textures/gui/resonance_stone/lock.png");
    public static final ResourceLocation FAMILIAR_BOX = CommonClass.customLocation("textures/gui/resonance_stone/familiar_box.png");
    public static final ResourceLocation FAMILIAR_BOX_SELECTED = CommonClass.customLocation("textures/gui/resonance_stone/familiar_box_selected.png");
    public static final int WIDTH = 94;
    public static final int HEIGHT = 19;

    private FamiliarHolder<?, ?> familiar;
    private FamiliarList parentList;
    private boolean locked;

    public FamiliarWidget(FamiliarList list, FamiliarHolder<?, ?> familiar, boolean locked) {
        this.familiar = familiar;
        this.parentList = list;
        this.locked = locked;
    }

    public void drawButton(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(
                this.locked ? LOCKED_BOX : isHovering(x, y, mouseX, mouseY) ? FAMILIAR_BOX_SELECTED : FAMILIAR_BOX,
                x, y,
                0, 0,
                WIDTH, HEIGHT,
                WIDTH, HEIGHT);

        if (this.locked)
            graphics.blit(
                    LOCK,
                    x + 75, y + 1,
                    0, 0,
                    16, 16,
                    16, 16);

        graphics.drawString(
                parentList.minecraft.font,
                familiar.getEntity().getDescription(),
                x + 5,
                y + 6,
                -1,
                false
        );
    }

    public void onClick() {
        if (this.locked) return;
        this.parentList.screen.setFamiliar(this.familiar);
    }

    public boolean isHovering(int x, int y, int mouseX, int mouseY) {
        int i = x + WIDTH;
        int j = y + HEIGHT;
        return mouseX > x && mouseX < i && mouseY > y && mouseY < j;
    }
}
