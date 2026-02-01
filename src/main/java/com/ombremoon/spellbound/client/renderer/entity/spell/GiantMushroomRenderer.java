package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.client.model.entity.spell.SpellBrokerModel;
import com.ombremoon.spellbound.client.renderer.types.GenericLivingEntityRenderer;
import com.ombremoon.spellbound.common.world.entity.living.wildmushroom.GiantMushroom;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class GiantMushroomRenderer extends GenericLivingEntityRenderer<GiantMushroom> {
    public static final ResourceLocation MUSHROOM_ANGRY = CommonClass.customLocation("textures/entity/giant_mushroom/giant_mushroom_angry.png");
    public static final ResourceLocation MUSHROOM_JUMP = CommonClass.customLocation("textures/entity/giant_mushroom/giant_mushroom_jump.png");

    public GiantMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(GiantMushroom animatable) {
        return animatable.isBouncingAndAirborne() ? MUSHROOM_JUMP : !animatable.hasSummoner() ? MUSHROOM_ANGRY : super.getTextureLocation(animatable);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, GiantMushroom animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.scale(6.0F, 6.0F, 6.0F);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
