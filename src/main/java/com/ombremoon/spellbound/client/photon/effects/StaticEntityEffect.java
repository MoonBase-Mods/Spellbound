package com.ombremoon.spellbound.client.photon.effects;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Objects;

public class StaticEntityEffect extends EntityEffectExecutor {
    private Vec3 effectPos;

    public StaticEntityEffect(FX fx, Level level, Entity entity, AutoRotate autoRotate) {
        super(fx, level, entity, autoRotate);
    }

    public void setPos(double x, double y, double z) {
        this.effectPos = new Vec3(x, y, z);
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        if (this.runtime != null && fxObject == this.runtime.root && autoRotate == AutoRotate.LOOK) {
            /*Vec3 lookAngle = entity.getLookAngle();
            Vec3 rightVector = lookAngle.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 upVector = rightVector.cross(lookAngle).normalize();
            Vec3 transformedOffset = rightVector.scale(offset.x)
                    .add(upVector.scale(offset.y))
                    .add(lookAngle.scale(offset.z));

            Vector3f pos = this.effectPos != null && !this.effectPos.equals(Vec3.ZERO)
                    ? this.effectPos.add(transformedOffset).toVector3f()
                    : entity.position().add(transformedOffset).toVector3f();
            runtime.root.updatePos(pos);*/
        }
    }

    private void applyRotation(Entity entity, AutoRotate autoRotate, IFXObject fxObject) {
        if (this.runtime != null && fxObject == this.runtime.root) {
            Quaternionf newRotation = rotation;
            if (autoRotate != AutoRotate.NONE) {
                switch (autoRotate) {
                    case FORWARD -> {
                        var forward = entity.getForward();
                        newRotation = new Quaternionf(rotation).rotateXYZ(
                                0,
                                (float) org.joml.Math.atan2(-forward.z, forward.x),
                                0
                        );
                    }
                    case LOOK -> {
                        newRotation = new Quaternionf(rotation).rotateXYZ(
                                Math.toRadians(-90),
                                Math.toRadians(-entity.getYRot()),
                                Math.toRadians(0)
                        );
                    }
                    case XROT -> {
                        newRotation = new Quaternionf(rotation).rotateXYZ(
                                0,
                                Math.toRadians(-90 - entity.getVisualRotationYInDegrees()),
                                0
                        );
                    }
                }
            }

            runtime.root.updateRotation(newRotation);
        }
    }

    @Override
    public void start() {
        if (!entity.isAlive()) return;

        var effects = CACHE.computeIfAbsent(entity, p -> new ArrayList<>());
        if (!allowMulti) {
            var iter = effects.iterator();
            while (iter.hasNext()) {
                var effect = iter.next();
                boolean removed = false;
                if (effect.getRuntime() != null && !effect.getRuntime().isAlive()) {
                    iter.remove();
                    removed = true;
                }
                if ((effect.fx.equals(fx) || Objects.equals(effect.fx.getFxLocation(), fx.getFxLocation())) && !removed) {
                    return;
                }
            }
        }

        this.runtime = fx.createRuntime();
        var root = this.runtime.getRoot();

        Vec3 lookAngle = entity.getForward();
        Vec3 rightVector = lookAngle.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 upVector = rightVector.cross(lookAngle).normalize();
        Vec3 transformedOffset = rightVector.scale(offset.x)
                .add(upVector.scale(offset.y))
                .add(lookAngle.scale(offset.z));

        Vector3f pos = this.effectPos != null && !this.effectPos.equals(Vec3.ZERO)
                ? this.effectPos.add(transformedOffset).toVector3f()
                : entity.position().add(transformedOffset).toVector3f();
        root.updatePos(pos);
        this.applyRotation(entity, autoRotate, root);
        root.updateScale(scale);
        this.runtime.emmit(this, delay);
        effects.add(this);
    }
}
