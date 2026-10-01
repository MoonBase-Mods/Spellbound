package com.ombremoon.spellbound.client.gui.familiar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FamiliarRenderButton {
    private FamiliarRenderWidget widget;
    private int x;
    private int y;
    private ResourceLocation texture;
    private ResourceLocation hoverTexture;
    private Consumer<ResonanceStoneScreen> consumer;
    private List<Component> tooltipComps;

    public FamiliarRenderButton(FamiliarRenderWidget widget, int x, int y, ResourceLocation texture, ResourceLocation hoverTexture, Consumer<ResonanceStoneScreen> onClick, Component... comps) {
        this.widget = widget;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.hoverTexture = hoverTexture;
        this.consumer = onClick;
        this.tooltipComps = Arrays.asList(comps);
    }

    public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hovering = isHovering(mouseX, mouseY);
        graphics.blit(
                hovering ? this.hoverTexture : this.texture,
                this.x, this.y,
                0, 0,
                17, 17,
                17, 17
        );

        if (hovering) {
            graphics.renderTooltip(
                    Minecraft.getInstance().font,
                    tooltipComps,
                    Optional.empty(),
                    mouseX, mouseY
            );
        }
    }

    public boolean isHovering(int mouseX, int mouseY) {
        int i = this.x + 17;
        int j = this.y + 17;
        return mouseX > this.x && mouseX < i && mouseY > y && mouseY < j;
    }

    public void consume(ResonanceStoneScreen screen) {
        this.consumer.accept(screen);
    }
}
