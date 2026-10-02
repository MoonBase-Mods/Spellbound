package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.gui.guide.elements.GuideMultiBlockElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IClickable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import com.ombremoon.spellbound.client.gui.guide.renderers.init.GuideBlockAndTintGetter;
import com.ombremoon.spellbound.client.gui.screens.GuideBookScreen;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.Map;

public class GuideMultiBlockRenderer implements IPageElementRenderer<GuideMultiBlockElement> {
    private static final ResourceLocation STRUCTURE_BG = ResourceLocation.withDefaultNamespace("advancements/title_box");
    private static final ResourceLocation CLOSE_BUTTON = ResourceLocation.withDefaultNamespace("advancements/title_box");

    private boolean isDetailed = false;
    private float xRot = 0;
    private float yRot = 0;

    @Override
    public void render(GuideMultiBlockElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        if (isDetailed) {
            if (!elementInDetailedView(element)) return;
            renderDetailedView(graphics, element);
            return;
        }

        GuideBlockAndTintGetter blockGetter = getTinter(element);
        Vec3 size = blockGetter.getSize();
        if (size == null) return;

        leftPos += element.position().xOffset();
        topPos += element.position().yOffset();
        int scale = element.scale();

        PoseStack poseStack = new PoseStack();

        //Setting up container
        Vec3 scaledSize = size.scale(scale);
        double angle = Math.toRadians(30);
        int maxWidth = (int) Math.sqrt((scaledSize.x*scaledSize.x) + (scaledSize.y*scaledSize.y));
        int maxHeight = (int) ((scaledSize.y*Math.cos(angle))+(scaledSize.z*Math.sin(angle)));
        maxHeight = Math.abs(maxHeight);
        graphics.blitSprite(STRUCTURE_BG, (int) (leftPos - ((maxWidth/2f)+scale)) - 3, (int) (topPos - scale - (maxHeight/2f))-6, (maxWidth)+(scale*2)+6, (maxHeight)+(scale*2)+12);


        poseStack.pushPose();
        poseStack.translate(leftPos, topPos, 100);
        poseStack.scale(-scale, scale, scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.XP.rotationDegrees(30));
        poseStack.mulPose(Axis.YP.rotationDegrees(tickCount + partialTick));
        poseStack.translate(-(size.x / 2f), -(size.y / 2f), -(size.z / 2f));

        RenderUtil.renderStructure(blockGetter, poseStack);
        poseStack.popPose();
    }

    private boolean elementInDetailedView(GuideMultiBlockElement element) {
        Object object = getData(element, "detailed");
        if (object instanceof Boolean bool) return bool;

        return false;
    }

    private GuideBlockAndTintGetter getTinter(GuideMultiBlockElement element) {
        Object rawData = getData(element, "block_getter");
        if (rawData instanceof GuideBlockAndTintGetter safeData) return safeData;
        else {
            boolean multiblock = element.structure().equals(GuideBookManager.FIRST_PAGE);
            var blockGetter = new GuideBlockAndTintGetter(multiblock ? element.multiblock() : element.structure(), multiblock);
            saveData(element, "block_getter", blockGetter);
            return blockGetter;
        }
    }

    private void renderDetailedView(GuiGraphics graphics, GuideMultiBlockElement element) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null)return;
        graphics.fill(0, 0, screen.width, screen.height, 0xCC000000);
        int structureScale = element.detailedScale();

        GuideBlockAndTintGetter blockGetter = getTinter(element);
        Vec3 size = blockGetter.getSize();

        if (screen.isDragging() && screen instanceof GuideBookScreen guideBook) {
            Pair<Double, Double> pair = guideBook.getMouseDrag();
            this.xRot += (float) (pair.getFirst() * 0.025f);
            this.yRot += (float) (pair.getSecond() * 0.025f);
        }

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(screen.width/2f, screen.height/2f, 1000);
        poseStack.scale(-structureScale, structureScale, structureScale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotation(this.xRot));
        poseStack.mulPose(Axis.XP.rotation(this.yRot));
        poseStack.translate(-(size.x / 2f), -(size.y/4f), -(size.z / 2f));

        //graphics.blitSprite(STRUCTURE_BG, 100, 10, screen.width- 200, screen.height-20);

        graphics.blitSprite(CLOSE_BUTTON, screen.width - 150, 15, 40, 22);
        graphics.drawString(Minecraft.getInstance().font,
                "Close",
                screen.width-145, 22    , 0xFFFFFFFF, false);
        RenderUtil.drawCenteredString(graphics, Minecraft.getInstance().font,
                Component.literal("Click and drag to rotate.").getVisualOrderText(),
                screen.width/2, screen.height-30, 0xFFFFFFFF, false);

        RenderUtil.renderStructure(blockGetter, poseStack);
        poseStack.popPose();
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideMultiBlockElement element) {
        if (isDetailed && elementInDetailedView(element)) return true;
        else if (isDetailed) return false;

        leftPos += element.position().xOffset();
        topPos += element.position().yOffset();

        var blockGetter = getTinter(element);
        Vec3 size = blockGetter.getSize();
        if (size == null) return false;
        Vec3 scaledSize = size.scale(element.scale());
        double angle = Math.toRadians(30);
        int maxWidth = (int) Math.sqrt((scaledSize.x*scaledSize.x) + (scaledSize.y*scaledSize.y));
        int maxHeight = (int) ((scaledSize.y*Math.cos(angle))+(scaledSize.z*Math.sin(angle)));
        maxHeight = Math.abs(maxHeight);

        int scale = element.scale();
        int i = (int) ((leftPos - ((maxWidth/2f)+scale)) - 3);
        int j = (int) (topPos - scale - (maxHeight/2f))-6;

        return mouseX > i && mouseX < i + (int) (maxWidth)+(scale*2)+6 &&
                mouseY >= j && mouseY < j + (int) (maxHeight)+(scale*2)+12;
    }

    @Override
    public void handleClick(GuideMultiBlockElement element, Screen screen, double mouseX, double mouseY, int leftPos, int topPos) {
        if (!isDetailed) {
            this.isDetailed = true;
            saveData(element, "detailed", true);
            return;
        }

        int i = screen.width - 150;
        int j = 15;

        if (mouseX > i && mouseX < i + 40 && mouseY > j && mouseY < j + 22 && elementInDetailedView(element)) {
            this.isDetailed = false;
            saveData(element, "detailed", false);
            this.xRot = 0;
            this.yRot = 0;
        }
    }

    @Override
    public void handleHover(GuideMultiBlockElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        if (isDetailed) return;

        guiGraphics.renderTooltip(
                Minecraft.getInstance().font,
                Component.translatable("guide.element.multiblock.open"),
                mouseX, mouseY);
    }
}
