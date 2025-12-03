package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.client.renderer.types.GenericLivingEntityRenderer;
import com.ombremoon.spellbound.common.world.entity.living.wildmushroom.MiniMushroom;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class MiniMushroomRenderer extends GenericLivingEntityRenderer<MiniMushroom> {
    public MiniMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MiniMushroom animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!animatable.isEnlarged())
            poseStack.scale(0.6F, 0.6F, 0.6F);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
