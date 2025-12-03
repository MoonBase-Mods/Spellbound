package com.ombremoon.spellbound.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.common.world.block.entity.TransfigurationDisplayBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TransfigurationDisplayRenderer implements BlockEntityRenderer<TransfigurationDisplayBlockEntity> {
    private final ItemRenderer itemRenderer;

    public TransfigurationDisplayRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(TransfigurationDisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        ItemStack itemstack = blockEntity.currentItem;
        if (itemstack != null && !itemstack.isEmpty()) {
            poseStack.translate(0.5F, 1.15F, 0.5F);
            float f = (float) blockEntity.itemTick + partialTick;
            poseStack.translate(0.0F, 0.1F + Mth.sin(f * 0.1F) * 0.01F, 0.0F);
            if (!blockEntity.active) {
                float f1 = blockEntity.rot - blockEntity.oRot;

                while (f1 >= (float) Math.PI) {
                    f1 -= (float) (Math.PI * 2);
                }

                while (f1 < (float) -Math.PI) {
                    f1 += (float) (Math.PI * 2);
                }

                float f2 = blockEntity.oRot + f1 * partialTick;
                poseStack.mulPose(Axis.YP.rotation(-f2));
            } else {
                Vec3 vec3 = blockEntity.spiralOffset(partialTick, 1.5);
                poseStack.translate(vec3.x, vec3.y, vec3.z);
            }

            this.itemRenderer.renderStatic(itemstack, ItemDisplayContext.GROUND, 15728880, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 0);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TransfigurationDisplayBlockEntity blockEntity) {
        return true;
    }
}
