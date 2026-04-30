package com.ombremoon.spellbound.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.photon.HeldItemTransformCapture;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void spellbound$captureHeldItemTransform(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
                                                     HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer,
                                                     int packedLight, CallbackInfo ci) {
        if (stack.isEmpty()) return;
        InteractionHand hand = arm == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        HeldItemTransformCapture.capture(entity.getId(), hand, poseStack);
    }
}
