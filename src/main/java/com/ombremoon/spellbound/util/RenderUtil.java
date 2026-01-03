package com.ombremoon.spellbound.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.gui.GuideBookScreen;
import com.ombremoon.spellbound.client.gui.WorkbenchScreen;
import com.ombremoon.spellbound.client.renderer.SBRenderTypes;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.Optional;

public class RenderUtil {

    public static void setupScreen(ResourceLocation resourceLocation) {
        setupScreen(resourceLocation, 1.0F);
    }

    public static void setupScreen(ResourceLocation resourceLocation, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.setShaderTexture(0, resourceLocation);
    }

    public static int getScaledRender(float current, int max, int size) {
        return max != 0 && current != 0 ? (int) (current * size / max) : 0;
    }

    public static void drawWordWrap(GuiGraphics guiGraphics, Font font, FormattedText text, int x, int y, int lineWidth, int color) {
        for (FormattedCharSequence formattedcharsequence : font.split(text, lineWidth)) {
            drawCenteredString(guiGraphics, font, formattedcharsequence, x, y, color);
            y += 9;
        }
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, FormattedCharSequence text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x - font.width(text) / 2, y - font.lineHeight / 2, color);
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        guiGraphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    public static void openWorkbench() {
        Minecraft.getInstance().setScreen(new WorkbenchScreen(Component.translatable("screen.spellbound.workbench")));
    }

    public static void openBook(ResourceLocation id, ResourceLocation bookTexture) {
        Minecraft.getInstance().setScreen(new GuideBookScreen(Component.translatable("screen.spellbound.guide_book"), id, bookTexture));
    }

    public static void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, float scale) {
        renderItem(graphics, stack, x, y, scale, 150.0F);
    }
    public static void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, float scale, float depth) {
        if (!stack.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate((x + (19*scale)), (y + (17*scale)), depth);

            try {
                float size = 16.0F * 1.2F * scale;
                pose.scale(size, -size, size);
                boolean flag = !bakedmodel.usesBlockLight();
                if (flag) {
                    Lighting.setupForFlatItems();
                }
                minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                graphics.flush();
                if (flag) {
                    Lighting.setupFor3DItems();
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                throw new ReportedException(crashreport);
            }

            pose.popPose();
        }
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Entity entity, boolean isVisible) {
        renderEntityInInventory(guiGraphics, x, y, scale, entity, isVisible, new Quaternionf(), false, 0, 0);
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Entity entity, boolean isVisible, Quaternionf mul,  boolean followsMouse, int mouseX, int mouseY) {
        renderEntityInInventory(guiGraphics,x, y, scale, entity, isVisible, mul, followsMouse, mouseX, mouseY, false);
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Entity entity, boolean isVisible, Quaternionf mul, boolean followsMouse, int mouseX, int mouseY, boolean rotates) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(x, y, 50);
        poseStack.mulPose(mul);

        if (followsMouse) {
            float f = (float)(x + x + entity.getBbWidth()) / 2.0F;
            float f1 = (float)(y +  y + entity.getBbHeight()) / 2.0F;
            float f2 = (float)Math.atan((double)((f - mouseX) / 40.0F));
            float f3 = (float)Math.atan((double)((f1 - mouseY) / 40.0F));

            Quaternionf quaternionf = new Quaternionf()
                    .rotateZ((float)Math.PI)         //flip
                    .rotateX(-f3 * 20.0F * ((float)Math.PI / 180F))  //vertical
                    .rotateY(f2);                //horizontal

            poseStack.mulPose(quaternionf);
        } else if (rotates) {
            poseStack.mulPose(new Quaternionf()
                    .rotateY((float) (Math.toRadians(entity.tickCount) % 360))
                    .rotateZ((float) Math.PI));
        } else {
            poseStack.mulPose(new Quaternionf()
                    .rotateZ((float) Math.PI));
        }

        poseStack.translate(-240, -130, 50);
        poseStack.scale(scale, scale, scale);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

        if (!isVisible) {
            RenderSystem.setShaderColor(0f, 0f, 0f, 1f);
        }

        if (entity instanceof ItemEntity itemEntity) {
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            renderer.renderStatic(itemEntity.getItem(),
                    ItemDisplayContext.GUI,
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffers,
                    itemEntity.level(),
                    0);
        } else {
            dispatcher.setRenderShadow(false);
            dispatcher.render(entity,
                    0,
                    0,
                    0,
                    0,
                    0,
                    poseStack,
                    buffers,
                    isVisible ? 15728880 : 0);
            buffers.endBatch();
            dispatcher.setRenderShadow(true);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);


        poseStack.popPose();
    }
}
