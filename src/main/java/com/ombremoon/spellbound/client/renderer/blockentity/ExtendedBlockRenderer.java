package com.ombremoon.spellbound.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.common.world.block.entity.ExtendedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public interface ExtendedBlockRenderer {

    default Function<ResourceLocation, RenderType> getRenderTypeFunction(ExtendedBlockPreviewRenderer.PreviewMode previewMode) {
       return previewMode.equals(ExtendedBlockPreviewRenderer.PreviewMode.PLACED) ? RenderType::entityCutout : RenderType::entityTranslucentCull;
    }

    default RenderType getRenderType(ExtendedBlockPreviewRenderer.PreviewMode previewMode, ResourceLocation location) {
        return previewMode.equals(ExtendedBlockPreviewRenderer.PreviewMode.PLACED) ? RenderType.entityCutout(location) : RenderType.entityTranslucentCull(location);
    }

    default VertexConsumer getConsumer(MultiBufferSource buffer, ExtendedBlockEntity blockEntity, Material materialBase, Material materialCorrupted, Block blockCorrupted) {
        ExtendedBlockPreviewRenderer.PreviewMode previewMode = blockEntity.previewMode;

        RenderType renderTypeBase = getRenderType(previewMode, materialBase.atlasLocation());
        RenderType renderTypeCorrupted = getRenderType(previewMode, materialCorrupted.atlasLocation());

        VertexConsumer baseConsumer = materialBase.sprite().wrap(buffer.getBuffer(renderTypeBase));
        VertexConsumer corruptedConsumer = materialCorrupted.sprite().wrap(buffer.getBuffer(renderTypeCorrupted));

        return blockEntity.getBlockState().is(blockCorrupted) ? corruptedConsumer : baseConsumer;
    }


    default Level level(){
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null){
            throw new IllegalStateException("Blockentity Level is null");
        }
        return level;
    }

    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, ExtendedBlockPreviewRenderer.PreviewMode previewMode) {
        render(modelPart, poseStack, vertexConsumer, packedLight, packedOverlay, 0xffffffff, previewMode);
    }

    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int r, int g, int b, int alpha, ExtendedBlockPreviewRenderer.PreviewMode previewMode) {
        render(modelPart, poseStack, vertexConsumer, packedLight, packedOverlay, FastColor.ARGB32.color(alpha, r, g, b), previewMode);
    }

    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color, ExtendedBlockPreviewRenderer.PreviewMode previewMode) {
        modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, colorFromPreviewMode(previewMode, color));
    }

    static int colorFromPreviewMode(ExtendedBlockPreviewRenderer.PreviewMode previewMode, int originalColor){
        float r = FastColor.ARGB32.red(originalColor);
        float g = FastColor.ARGB32.green(originalColor);
        float b = FastColor.ARGB32.blue(originalColor);
        float a = FastColor.ARGB32.alpha(originalColor);

        switch (previewMode) {
            case PREVIEW -> a *= ExtendedBlockPreviewRenderer.PreviewMode.PREVIEW.alpha;

            case INVALID -> {
                r *= ExtendedBlockPreviewRenderer.PreviewMode.INVALID.red;
                g *= ExtendedBlockPreviewRenderer.PreviewMode.INVALID.green;
                b *= ExtendedBlockPreviewRenderer.PreviewMode.INVALID.blue;
                a *= ExtendedBlockPreviewRenderer.PreviewMode.INVALID.alpha;
            }
        }
        return FastColor.ARGB32.color((int) a, (int) r, (int) g, (int) b);
    }
}
