package com.ombremoon.spellbound.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.common.world.block.RuneBlock;
import com.ombremoon.spellbound.common.world.block.entity.RuneBlockEntity;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RuneBlockRenderer implements BlockEntityRenderer<RuneBlockEntity> {

    public RuneBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RuneBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int runeType = blockEntity.getBlockState().getValue(RuneBlock.RUNE_TYPE);
        int color = blockEntity.getData(SBData.RUNE_COLOR);
        this.renderRune(poseStack.last(), bufferSource.getBuffer(RenderType.entityCutoutNoCull(createRuneTexture(runeType))), color, packedLight, packedOverlay);
    }

    private void renderRune(PoseStack.Pose pose, VertexConsumer consumer, int color, int packedLight, int packedOverlay) {
        this.renderFace(pose, consumer, color, 0.0F, 1.0F, 0.05F, 0.0F, 1.0F, packedLight, packedOverlay);
    }

    private void renderFace(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int color,
            float x0,
            float x1,
            float y,
            float z0,
            float z1,
            int packedLight,
            int packedOverlay
    ) {
        addVertex(pose, consumer, color, x0, y, z1, 0.0F, 1.0F, packedLight, packedOverlay);
        addVertex(pose, consumer, color, x1, y, z1, 1.0F, 1.0F, packedLight, packedOverlay);
        addVertex(pose, consumer, color, x1, y, z0, 1.0F, 0.0F, packedLight, packedOverlay);
        addVertex(pose, consumer, color, x0, y, z0, 0.0F, 0.0F, packedLight, packedOverlay);
    }

    private static void addVertex(
            PoseStack.Pose pose, VertexConsumer consumer, int color, float x, float y, float z, float u, float v, int packedLight, int packedOverlay
    ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);;
    }

    private ResourceLocation createRuneTexture(int runeType) {
        return CommonClass.customLocation("textures/block/rune/rune_" + runeType + ".png");
    }
}
