package com.ombremoon.spellbound.client.photon.effects;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Objects;

public class EntityDeathEffect extends EntityEffectExecutor {
    public EntityDeathEffect(FX fx, Level level, Entity entity, AutoRotate autoRotate) {
        super(fx, level, entity, autoRotate);
    }

    @Override
    public void updateFXObjectTick(IFXObject fxObject) {
        if (runtime != null && fxObject == runtime.root) {
            if (entity.isRemoved()) {
                runtime.destroy(forcedDeath);
                CACHE.computeIfAbsent(entity, p -> new ArrayList<>()).remove(this);
                if (CACHE.get(entity).isEmpty()) {
                    CACHE.remove(entity);
                }
            }
        }
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        if (runtime != null && fxObject == runtime.root) {
            if (autoRotate != AutoRotate.NONE) {
                switch (autoRotate) {
                    case FORWARD -> {
                        var forward = entity.getForward();
                        var newRotation = new Quaternionf(rotation).rotateXYZ(
                                0,
                                (float) org.joml.Math.atan2(-forward.z, forward.x),
                                (float) forward.y
                        );
                        runtime.root.updateRotation(newRotation);
                    }
                    case LOOK -> {
                        var lookAngles = entity.getLookAngle();
                        var newRotation = new Quaternionf(rotation).rotateXYZ(
                                0,
                                (float) org.joml.Math.atan2(-lookAngles.z, lookAngles.x),
                                (float) lookAngles.y
                        );
                        runtime.root.updateRotation(newRotation);
                    }
                    case XROT -> {
                        var newRotation = new Quaternionf(rotation).rotateXYZ(
                                0,
                                Math.toRadians(-90 - entity.getVisualRotationYInDegrees()),
                                0
                        );
                        runtime.root.updateRotation(newRotation);
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        if (entity.isRemoved())
            return;

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
        root.updatePos(entity.getEyePosition().toVector3f().add(offset.x, offset.y, offset.z));
        root.updateRotation(rotation);
        root.updateScale(scale);
        this.runtime.emmit(this, delay);
        effects.add(this);
    }
}
