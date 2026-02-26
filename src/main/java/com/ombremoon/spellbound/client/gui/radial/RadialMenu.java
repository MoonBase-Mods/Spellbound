package com.ombremoon.spellbound.client.gui.radial;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.util.Color;

import java.util.List;

/**
 * Adapted from <a href="https://github.com/gigaherz/ToolBelt">...</a> under the following license:
 * <p>
 * Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the author nor the
 * names of the contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class RadialMenu {
    public static final float OPEN_ANIM_LENGTH = 2.5F;
    private static final float PRECISION = 2.5f / 360.0f;
    private static final double TWO_PI = 2.0 * Math.PI;
    public final int backgroundColor;
    public final int backgroundColorHover;
    private Screen screen;
    private DrawingContext.IDrawingHelper drawingHelper;
    private List<RadialMenuItem> items;
    private State state = State.INITIALIZING;
    private double startAnimation;
    public float animProgress;
    private float radiusIn;
    private float radiusOut;
    private float itemRadius;
    public float animTop;
    private Component centralText;

    public RadialMenu(final Screen screen, List<RadialMenuItem> items, final float radiusIn, final float radiusOut, final int backgroundColor, final int backgroundColorHover) {
        this.screen = screen;
        this.drawingHelper = (poseStack, mouseX, mouseY) -> {};
        this.items = items;
        this.radiusIn = radiusIn;
        this.radiusOut = radiusOut;
        this.itemRadius = (radiusIn + radiusOut) / 2.0F;
        this.backgroundColor = backgroundColor;
        this.backgroundColorHover = backgroundColorHover;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void setCentralText(@Nullable Component centralText) {
        this.centralText = centralText;
    }

    public Component getCentralText() {
        return centralText;
    }
    @Nullable
    public RadialMenuItem getHoveredItem() {
        for (RadialMenuItem item : items)
        {
            if (item.isHovered())
                return item;
        }
        return null;
    }

    private void setHovered(int which) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setHovered(i == which);
        }
    }

    public void resize() {
        this.state = State.INITIALIZING;
    }

    public void clickItem() {
        switch (state) {
            case DEFAULT:
                RadialMenuItem item = getHoveredItem();
                if (item != null)
                {
                    item.onClick();
                    return;
                }
                break;
            default:
                break;
        }
        onClickOutside();
    }

    public void onClickOutside() {

    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public boolean isReady() {
        return state == State.DEFAULT;
    }

    public void clear() {
        items.clear();
    }

    public void close() {
        state = State.CLOSING;
        startAnimation = screen.getMinecraft().level.getGameTime() + (double) screen.getMinecraft().getTimer().getGameTimeDeltaPartialTick(false);
        animProgress = 1.0f;
        setHovered(-1);
    }

    public void tick() {
        if (state == State.INITIALIZING) {
            startAnimation = screen.getMinecraft().level.getGameTime() + (double) screen.getMinecraft().getTimer().getGameTimeDeltaPartialTick(false);
            state = State.OPENING;
            animProgress = 0;
        }
    }

    public void draw(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        updateAnimationState(partialTicks);

        if (isClosed())
            return;

        if (isReady())
            processMouse(mouseX, mouseY);

        Font font = screen.getMinecraft().font;

        boolean animated = state == State.OPENING || state == State.CLOSING;
        radiusIn = animated ? Math.max(0.1f, 40 * animProgress) : 40;
        radiusOut = radiusIn * 2;
        itemRadius = (radiusIn + radiusOut) * 0.5f;
        animTop = animated ? (1 - animProgress) * screen.height / 2.0f : 0;

        int x = screen.width / 2;
        int y = screen.height / 2;
        float z = 0;

        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, animTop, 0);

        drawBackground(graphics, x, y, z, radiusIn, radiusOut);

        poseStack.popPose();

        if (isReady()) {
            poseStack.pushPose();
            drawItems(graphics, x, y, z, screen.width, screen.height, font);
            poseStack.popPose();

            Component currentCentralText = centralText;
            for (RadialMenuItem item : this.items) {
                if (item.isHovered()) {
                    if (item.getCentralText() != null)
                        currentCentralText = item.getCentralText();
                    break;
                }
            }

            if (currentCentralText != null) {
                String text = currentCentralText.getString();
                float textX = (screen.width - font.width(text)) / 2.0f;
                float textY = (screen.height - font.lineHeight) / 2.0f;
                graphics.drawString(font, text, textX, textY, 0xFFFFFFFF, true);
            }

            poseStack.pushPose();
            drawTooltips(graphics, mouseX, mouseY);
            poseStack.popPose();
        }
    }

    private void updateAnimationState(float partialTicks) {
        float openAnimation = 0;
        switch (state) {
            case OPENING:
                openAnimation = (float) ((screen.getMinecraft().level.getGameTime() + partialTicks - startAnimation) / OPEN_ANIM_LENGTH);
                if (openAnimation >= 1.0 || this.items.isEmpty()) {
                    openAnimation = 1;
                    state = State.DEFAULT;
                }
                break;
            case CLOSING:
                openAnimation = 1 - (float) ((screen.getMinecraft().level.getGameTime() + partialTicks - startAnimation) / OPEN_ANIM_LENGTH);
                if (openAnimation <= 0 || this.items.isEmpty()) {
                    openAnimation = 0;
                    state = State.CLOSED;
                }
                break;
        }
        animProgress = openAnimation;
    }

    public void drawBackground(GuiGraphics guiGraphics, float x, float y, float z, float radiusIn, float radiusOut) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        iterateVisible((item, s, e) -> {
            int color = item.isHovered() || item.isSelected() ? backgroundColorHover : backgroundColor;
            drawPieArc(builder, x, y, z, radiusIn, radiusOut, s, e, color);
        });
        BufferUploader.drawWithShader(builder.buildOrThrow());

        // Draw red outline on outer edgevar outlineBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        ////        drawOuterEdge(outlineBuilder, x, y, z, radiusOut, 0xFFFF0000); // Red color
        ////        BufferUploader.drawWithShader(outlineBuilder.buildOrThrow());
//

        RenderSystem.disableBlend();
    }

    private void drawOuterEdge(BufferBuilder buffer, float x, float y, float z, float radius, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int a = (color >> 24) & 0xFF;

        // Draw a complete circle using line strip
        int segments = 360; // Number of segments for a smooth circle
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * TWO_PI / segments);
            float posX = x + radius * (float) Math.cos(angle);
            float posY = y + radius * (float) Math.sin(angle);
            buffer.addVertex(posX, posY, z).setColor(r, g, b, a);
        }
    }

    private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int color) {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, Mth.ceil(angle / PRECISION));

        angle = endAngle - startAngle;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int a = (color >> 24) & 0xFF;

        float slice = angle / sections;

        for (int i = 0; i < sections; i++) {
            float angle1 = startAngle + i * slice;
            float angle2 = startAngle + (i + 1) * slice;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.addVertex(pos1OutX, pos1OutY, z).setColor(r, g, b, a / 4);
            buffer.addVertex(pos1InX, pos1InY, z).setColor(r, g, b, a);
            buffer.addVertex(pos2InX, pos2InY, z).setColor(r, g, b, a);
            buffer.addVertex(pos2OutX, pos2OutY, z).setColor(r, g, b, a / 4);
        }
    }

    private void drawArrow(GuiGraphics guiGraphics, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = guiGraphics.guiWidth() / 2;
        int j = guiGraphics.guiHeight() / 2;

        var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int color = this.backgroundColorHover;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int a = (color >> 24) & 0xFF;

        builder.addVertex(i, j + 10, z).setColor(r, b, g, a);
        builder.addVertex(i, j - 10, z).setColor(r, b, g, a);
        builder.addVertex(i + 20, j - 10, z).setColor(r, b, g, a);
        builder.addVertex(i + 20, j + 10, z).setColor(r, b, g, a);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    private void drawTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        for (RadialMenuItem item : this.items) {
            if (item.isHovered()) {
                DrawingContext context = new DrawingContext(graphics, screen.width, screen.height, mouseX, mouseY, 0, this.items.size(), screen.getMinecraft().font, drawingHelper);
                item.drawTooltips(context);
            }
        }
    }

    private void drawItems(GuiGraphics graphics, int x, int y, float z, int width, int height, Font font) {
        iterateVisible((item, s, e) -> {
            float middle = (s + e) * 0.5f;
            float posX = x + itemRadius * (float) Math.cos(middle);
            float posY = y + itemRadius * (float) Math.sin(middle);

            DrawingContext context = new DrawingContext(graphics, width, height, posX, posY, z, this.items.size(), font, drawingHelper);
            item.draw(context);
        });
    }

    private void iterateVisible(TriConsumer<RadialMenuItem, Float, Float> consumer) {
        int numItems = items.size();
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            RadialMenuItem item = items.get(i);
            consumer.accept(item, s, e);
        }
    }

    private void processMouse(int mouseX, int mouseY) {
        if (!isReady())
            return;

        int numItems = this.items.size();

        int x = screen.width / 2;
        int y = screen.height / 2;
        double a = Math.atan2(mouseY - y, mouseX - x);
        double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (numItems > 0) {
            double s0 = getAngleFor(0 - 0.5, numItems);
            double s1 = getAngleFor(numItems - 0.5, numItems);
            while (a < s0) {
                a += TWO_PI;
            }
            while (a >= s1) {
                a -= TWO_PI;
            }
        }

        int hovered = -1;
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            if (a >= s && a < e && d >= radiusIn && d < radiusOut) {
                hovered = i;
                break;
            }
        }
        setHovered(hovered);
    }

    private double getAngleFor(double i, int numItems) {
        if (numItems == 0)
            return 0;
        return ((i / numItems) + 0.25) * TWO_PI + Math.PI;
    }

    public enum State {
        INITIALIZING,
        OPENING,
        DEFAULT,
        CLOSING,
        CLOSED,
    }
}
