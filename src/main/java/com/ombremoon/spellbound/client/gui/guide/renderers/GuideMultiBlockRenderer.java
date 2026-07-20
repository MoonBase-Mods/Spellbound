package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.gui.guide.elements.GuideMultiBlockElement;
import com.ombremoon.spellbound.client.gui.guide.elements.IPageElement;
import com.ombremoon.spellbound.client.gui.guide.renderers.init.GuideBlockAndTintGetter;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.Map;

public class GuideMultiBlockRenderer implements IPageElementRenderer<GuideMultiBlockElement> {
    private TextureTarget renderTarget;

    @Override
    public void render(GuideMultiBlockElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        GuideBlockAndTintGetter blockGetter = new GuideBlockAndTintGetter(Minecraft.getInstance().level, ResourceLocation.withDefaultNamespace("fossil/skull_1"));
        Vec3 size = blockGetter.getSize();
        if (size == null) return;

        RenderSystem.enableDepthTest();

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(graphics.guiWidth()/ 2f, graphics.guiHeight() / 2f, 1);
        poseStack.scale(20, 20, 20);
        poseStack.translate(-(size.x/2f), -(size.y/2f), -(size.z/2f));
        //poseStack.mulPose(Axis.YP.rotation(45));

        for (Map.Entry<BlockPos, BlockState> entry : blockGetter.getBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            FluidState fluid = blockGetter.getFluidState(pos);
            RandomSource random = Minecraft.getInstance().level.getRandom();

            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

            if (!fluid.isEmpty()) {
                RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluid);
                VertexConsumer consumer = bufferSource.getBuffer(renderType);
                blockRenderer.renderLiquid(
                        pos,
                        blockGetter,
                        consumer,
                        state,
                        fluid
                );
            }

            if (state.getRenderShape() == RenderShape.INVISIBLE) continue;
            BakedModel model = blockRenderer.getBlockModel(state);
            for (RenderType renderType : model.getRenderTypes(state, random, ModelData.EMPTY)) {
                VertexConsumer consumer = bufferSource.getBuffer(renderType);
                blockRenderer.renderBatched(
                        state,
                        pos,
                        blockGetter,
                        poseStack,
                        consumer,
                        true,
                        random
                );
            }

            poseStack.popPose();
        }

        bufferSource.endBatch();
        poseStack.popPose();

        RenderSystem.disableDepthTest();
    }
}
