package com.ombremoon.spellbound.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Utility for calculating world positions of items in entity hands.
 * Replicates the transformation logic from ItemInHandLayer.
 */
public class ItemPositionCalculator {

    /**
     * Calculates the world position of an item in the entity's hand.
     *
     * @param entity The living entity holding the item
     * @param arm Which arm (LEFT or RIGHT)
     * @param poseStack The current PoseStack from rendering (must include hand transformations)
     * @param model The entity's model (must implement ArmedModel)
     * @return The world position of the item
     */
    public static Vec3 getItemWorldPosition(LivingEntity entity, HumanoidArm arm, PoseStack poseStack, EntityModel<?> model) {
        if (!(model instanceof ArmedModel armedModel)) {
            return entity.position();
        }

        // Start with entity's world position
        Vec3 worldPos = entity.position();

        // Create a copy of the PoseStack to avoid modifying the original
        PoseStack tempStack = new PoseStack();

        // Apply entity rotation (yaw and pitch)
        tempStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        tempStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));

        // Apply all transformations from the original PoseStack
        for (PoseStack.Pose matrix : poseStack.poseStack) {
            tempStack.last().pose().mul(matrix.pose());
        }

        // Apply the hand-specific transformations (from ItemInHandLayer)
        applyHandTransformations(tempStack, armedModel, arm);

        // Extract the final position from the transformation matrix
        Matrix4f finalMatrix = tempStack.last().pose();
        Vector4f position = new Vector4f(0, 0, 0, 1);
        finalMatrix.transform(position);

        return new Vec3(
            worldPos.x + position.x,
            worldPos.y + position.y,
            worldPos.z + position.z
        );
    }

    /**
     * Applies the same transformations used in ItemInHandLayer for hand positioning.
     */
    private static void applyHandTransformations(PoseStack poseStack, ArmedModel armedModel, HumanoidArm arm) {
        // Translate to hand position (this is the key transformation)
        // Note: This requires access to the model's translateToHand method
        // You may need to implement this differently depending on your model

        // Apply the rotations from ItemInHandLayer
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        // Apply the final translation
        boolean isLeft = arm == HumanoidArm.LEFT;
        poseStack.translate((float)(isLeft ? -1 : 1) / 16.0F, 0.125F, -0.625F);
    }

    /**
     * Alternative approach: Extract position directly from PoseStack matrix.
     * This assumes the PoseStack already contains all necessary transformations.
     */
    public static Vec3 getItemPositionFromPoseStack(LivingEntity entity, PoseStack poseStack) {
        // Get the final transformation matrix
        Matrix4f matrix = poseStack.last().pose();

        // The translation components of the matrix give us the local position
        // We need to transform this by the entity's world transform
        Vector4f localPos = new Vector4f(0, 0, 0, 1);
        matrix.transform(localPos);

        // Apply entity rotation to get world position
        Vec3 entityPos = entity.position();
        float yaw = entity.getYRot() * (float) Math.PI / 180.0F;
        float pitch = entity.getXRot() * (float) Math.PI / 180.0F;

        // Rotate the local position by entity rotation
        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);

        double x = localPos.x * cosYaw - localPos.z * sinYaw;
        double z = localPos.x * sinYaw + localPos.z * cosYaw;
        double y = localPos.y * cosPitch - localPos.z * sinPitch;

        return new Vec3(entityPos.x + x, entityPos.y + y, entityPos.z + z);
    }

    /**
     * Simplified version assuming you have direct access to the hand position.
     * This is useful if you can get the hand position from the model.
     */
    public static Vec3 getItemPositionSimple(LivingEntity entity, Vec3 handOffset) {
        // Apply entity rotation to the hand offset
        float yaw = entity.getYRot() * (float) Math.PI / 180.0F;
        float pitch = entity.getXRot() * (float) Math.PI / 180.0F;

        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);

        double x = handOffset.x * cosYaw - handOffset.z * sinYaw;
        double z = handOffset.x * sinYaw + handOffset.z * cosYaw;
        double y = handOffset.y;

        Vec3 entityPos = entity.position();
        return new Vec3(entityPos.x + x, entityPos.y + y, entityPos.z + z);
    }
}
