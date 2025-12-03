package com.ombremoon.spellbound.client.renderer.types;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.client.model.MageArmorModel;
import com.ombremoon.spellbound.common.world.item.MageArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MageArmorRenderer extends GeoArmorRenderer<MageArmorItem> {
    public MageArmorRenderer() {
        super(new MageArmorModel());
    }

    @Override
    public void renderFinal(PoseStack poseStack, MageArmorItem animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);
//        Constants.LOG.info("{}", model.getBone("bipedHead").get().getWorldSpaceMatrix());
    }
}
