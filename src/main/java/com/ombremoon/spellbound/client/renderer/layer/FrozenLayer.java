package com.ombremoon.spellbound.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;

public class FrozenLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public FrozenLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
//        if (!livingEntity.isInvisible()) {
//            ResourceLocation location = this.getTextureLocation(livingEntity);
//            renderColoredCutoutModel(this.getParentModel(), location, poseStack, bufferSource, packedLight, livingEntity, FastColor.ARGB32.color(153, 0x66, 0xCC, 0xFF));
//        }
    }
}
