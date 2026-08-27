package com.ombremoon.spellbound.util.math;

import net.minecraft.world.phys.Vec3;

/**
 * Represents a local 3D coordinate frame for spline-based projectile movement.
 * Establishes three orthonormal axes:
 * - X-axis: perpendicular to the target direction (horizontal deflection)
 * - Y-axis: vertical axis (height deviation)
 * - Z-axis: direction toward target (along the primary path)
 */
public class CurveFrame {
    private final Vec3 originX;  // Right vector (perpendicular to path)
    private final Vec3 originY;  // Up vector (vertical)
    private final Vec3 originZ;  // Forward vector (toward target)

    /**
     * Constructs a CurveFrame from shooter position to target position.
     * 
     * @param shooterPos Starting position of the projectile
     * @param targetPos Target position
     */
    public CurveFrame(Vec3 shooterPos, Vec3 targetPos) {
        Vec3 direction = targetPos.subtract(shooterPos);
        this.originZ = direction.normalize();
        this.originY = new Vec3(0, 1, 0);

        if (Math.abs(this.originZ.y) > 0.99) {
            this.originX = new Vec3(1, 0, 0).normalize();
        } else {
            this.originX = this.originZ.cross(this.originY).normalize();
        }
    }

    /**
     * Converts a local spline offset (in frame coordinates) to world coordinates.
     * 
     * @param offsetX Offset along the X-axis (perpendicular to path)
     * @param offsetY Offset along the Y-axis (vertical)
     * @param offsetZ Offset along the Z-axis (toward target)
     * @return The offset as a Vec3 in world coordinates
     */
    public Vec3 toWorldCoordinates(double offsetX, double offsetY, double offsetZ) {
        return this.originX.scale(offsetX)
                .add(this.originY.scale(offsetY))
                .add(this.originZ.scale(offsetZ));
    }

    /**
     * Gets the X-axis (perpendicular to target direction).
     */
    public Vec3 getXAxis() {
        return this.originX;
    }

    /**
     * Gets the Y-axis (vertical).
     */
    public Vec3 getYAxis() {
        return this.originY;
    }

    /**
     * Gets the Z-axis (toward target).
     */
    public Vec3 getZAxis() {
        return this.originZ;
    }
}
