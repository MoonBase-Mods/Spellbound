package com.ombremoon.spellbound.util.math;

import com.ombremoon.spellbound.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Controls projectile trajectory using directional gravity (acceleration).
 * 
 * The controller applies a gravity vector (acceleration) along a specified axis
 * until the projectile reaches a peak point along the shooter->target line.
 * After the peak, gravity stops (projectile continues with constant velocity + normal gravity).
 * 
 * Example: If A=(0,0,0), B=(0,0,10), W=0.5, V=(0,1,0):
 * - Projectile arcs upward for the first 5 blocks (0 to 0.5 along line)
 * - Then continues forward (with standard gravity applying if not already handled)
 */

public class SplineController {
    public static final StreamCodec<ByteBuf, SplineController> STREAM_CODEC = StreamCodec.composite(
            SerializationUtil.VEC3_STREAM_CODEC, SplineController::getShooterPos,
            SerializationUtil.VEC3_STREAM_CODEC, SplineController::getTargetPos,
            ControlPoint.STREAM_CODEC.apply(ByteBufCodecs.list()), SplineController::getControlPoints,
            SplineController::new
    );
    private final Set<ControlPoint> completedSplines = new ObjectOpenHashSet<>();
    private final CurveFrame frame;
    private final Vec3 shooterPos;
    private final Vec3 targetPos;
    private final List<ControlPoint> controlPoints = new ArrayList<>();
    private final double maxDistance;

    /**
     * Creates a trajectory controller with directional gravity.
     * 
     * @param shooterPos Starting position
     * @param targetPos Target position
     */
    public SplineController(Vec3 shooterPos, Vec3 targetPos, List<ControlPoint> controlPoints) {
        this.shooterPos = shooterPos;
        this.targetPos = targetPos;
        this.frame = new CurveFrame(shooterPos, targetPos);
        this.controlPoints.addAll(controlPoints);
//        this.peakParameter = Mth.clamp(peakParameter, 0, 1.0);
//        this.gravityVector = gravityVector;
        this.maxDistance = shooterPos.distanceTo(targetPos);
    }

    public static SplineController empty() {
        return new SplineController(Vec3.ZERO, Vec3.ZERO, List.of());
    }

    public static SplineController createSpline(Vec3 origin, Vec3 endPoint) {
        return new SplineController(origin, endPoint, new ArrayList<>());
    }

    public SplineController addControlPoint(double peakParameter, Vec3 gravityVector) {
        this.controlPoints.add(new ControlPoint(peakParameter, gravityVector));
        return new SplineController(this.shooterPos, this.targetPos, this.controlPoints);
    }

    public boolean isEmptySpline() {
        return this.controlPoints.isEmpty();
    }

    /**
     * Determines if gravity should be applied at the current position.
     * Gravity applies based on the projectile's position along the shooter->target line.
     * 
     * @param currentPos The projectile's current position
     * @return The gravity vector to apply (in world space), or zero vector if past peak
     */
    public Vec3 getGravityAtPosition(Vec3 currentPos) {
        double t = getProjectionParameter(currentPos);
        for (ControlPoint point : this.controlPoints) {
            double peakParameter = point.peakParameter();
            Vec3 gravity = point.gravityVector();
            if (t < peakParameter) {
                return this.frame.toWorldCoordinates(
                        gravity.x,
                        gravity.y,
                        gravity.z
                );
            } else {
                this.completedSplines.add(point);
            }
        }
        
        return Vec3.ZERO;
    }

    /**
     * Calculates where the projectile is along the shooter->target line.
     * This is done by projecting the projectile's position onto the line.
     * 
     * @param currentPos The projectile's current position
     * @return Parameter t from 0 to 1, where 0=shooter and 1=target
     */
    private double getProjectionParameter(Vec3 currentPos) {
        Vec3 toTarget = this.targetPos.subtract(this.shooterPos);
        Vec3 toCurrentPos = currentPos.subtract(this.shooterPos);
        double dotProduct = toCurrentPos.dot(toTarget);
        double lengthSquared = toTarget.dot(toTarget);
        
        if (lengthSquared <= 0)
            return 0;
        
        double t = dotProduct / lengthSquared;
        return Mth.clamp(t, 0, 1.0);
    }

    private double getPerpendicularDistance(Vec3 currentPos) {
        double t = getProjectionParameter(currentPos);
        Vec3 projection = this.shooterPos.add(this.targetPos.subtract(this.shooterPos).scale(t));
        Vec3 perp = currentPos.subtract(projection);
        return perp.length();
    }

    public boolean isFollowingSpline() {
        return this.completedSplines.size() < this.controlPoints.size();
    }

    /**
     * Gets the peak position along the shooter->target line.
     * This is where gravity stops being applied.
     * 
     * @return The position at the peak point
     */
    /*public Vec3 getPeakPosition() {
        Vec3 direction = this.targetPos.subtract(this.shooterPos);
        return this.shooterPos.add(direction.scale(this.peakParameter));
    }*/

    /**
     * Gets the projection parameter of a position along the line.
     * Public version of the private method for debugging.
     * 
     * @param pos Position to project
     * @return Parameter from 0 to 1
     */
    public double getPositionParameter(Vec3 pos) {
        return getProjectionParameter(pos);
    }

    /**
     * Gets the shooter position.
     */
    public Vec3 getShooterPos() {
        return this.shooterPos;
    }

    /**
     * Gets the target position.
     */
    public Vec3 getTargetPos() {
        return this.targetPos;
    }

    public List<ControlPoint> getControlPoints() {
        return this.controlPoints;
    }

/*    *//**
     * Gets the peak parameter (0 to 1).
     *//*
    public double getPeakParameter() {
        return this.peakParameter;
    }

    *//**
     * Gets the gravity vector being applied.
     *//*
    public Vec3 getGravityVector() {
        return this.gravityVector;
    }*/

    /**
     * Gets the maximum distance (shooter to target).
     */
    public double getMaxDistance() {
        return this.maxDistance;
    }
}
