package com.ombremoon.spellbound.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.client.model.blockentity.ValkyrStatueModel;
import com.ombremoon.spellbound.common.world.block.AbstractExtendedBlock;
import com.ombremoon.spellbound.common.world.block.entity.ValkyrBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ValkyrStatueRenderer extends GeoBlockRenderer<ValkyrBlockEntity> {
    public ValkyrStatueRenderer(BlockEntityRendererProvider.Context context) {
        super(new ValkyrStatueModel());
    }

    @Override
    public @Nullable RenderType getRenderType(ValkyrBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (!animatable.previewMode.equals(ExtendedBlockPreviewRenderer.PreviewMode.PLACED)){
            return RenderType.entityTranslucentCull(texture);
        }
        return RenderType.entityCutout(texture);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ValkyrBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!animatable.getBlockState().getValue(AbstractExtendedBlock.CENTER)) return;

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, ExtendedBlockRenderer.colorFromPreviewMode(animatable.previewMode, colour));
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
        }
    }
}
