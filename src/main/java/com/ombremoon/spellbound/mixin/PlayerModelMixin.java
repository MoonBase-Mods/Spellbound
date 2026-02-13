package com.ombremoon.spellbound.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
    
    public PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void renderArmWithItem(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci) {
        if (entity instanceof Player player) {
            PoseStack stack = new PoseStack();
            HumanoidArm arm = HumanoidArm.RIGHT;
            this.translateToHand(arm, stack);
            stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            stack.mulPose(Axis.YP.rotationDegrees(180.0F));
            stack.translate(1 / 16.0F, 0.125F, -0.625F);

            Matrix4f matrix = stack.last().pose();
            Vector4f handOffset = new Vector4f(0, 0, 0, 1);
            handOffset.mul(matrix);
            Vec3 localOffset = new Vec3(handOffset.x, handOffset.y, handOffset.z);

            float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            double xPos = player.xo + (player.getX() - player.xo) * partialTicks;
            double yPos = player.yo + (player.getY() - player.yo) * partialTicks;
            double zPos = player.zo + (player.getZ() - player.zo) * partialTicks;
            Vec3 playerPos = new Vec3(xPos, yPos, zPos);
            float yaw = player.yBodyRot;
            Vec3 rotatedOffset = new Vec3(localOffset.x, -localOffset.y, localOffset.z).yRot((float) -Math.toRadians(yaw));
            SpellUtil.getSpellHandler(player).handPos = playerPos.add(0, player.getEyeHeight(), 0).add(rotatedOffset);
        }
    }

    private static Vec3 getPosFromPoseStack(PoseStack poseStack) {
        Matrix4f matrix = poseStack.last().pose();
        Vector4f pos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        pos.mul(matrix);
        return new Vec3(pos.x, pos.y, pos.z);
    }
}
