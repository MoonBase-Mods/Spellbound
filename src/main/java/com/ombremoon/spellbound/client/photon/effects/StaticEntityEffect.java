package com.ombremoon.spellbound.client.photon.effects;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXData;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.Constant;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction3;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.ombremoon.spellbound.main.Constants;
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
            Vec3 lookAngle = entity.getLookAngle();
            Vec3 rightVector = lookAngle.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 upVector = rightVector.cross(lookAngle).normalize();
            Vec3 transformedOffset = rightVector.scale(offset.x)
                    .add(upVector.scale(offset.y))
                    .add(lookAngle.scale(offset.z));

            Vector3f pos = this.effectPos != null && !this.effectPos.equals(Vec3.ZERO)
                    ? this.effectPos.add(transformedOffset).toVector3f()
                    : entity.position().add(transformedOffset).toVector3f();
            runtime.root.updatePos(pos);
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
                                Math.toRadians(-entity.getXRot()),
                                Math.toRadians(-entity.getYRot() - 90),
                                Math.toRadians(0)
                        );
                        /*var lookAngles = entity.getLookAngle();
                        newRotation = new Quaternionf(rotation).rotateXYZ(
                                0,
                                (float) org.joml.Math.atan2(-lookAngles.z, lookAngles.x),
                                (float) lookAngles.y
                        );*/
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

//        FXData data = fx.getFxData().copy(false);
        FXData testData = fx.getFxData();
        var listo = new ArrayList<>(testData.objects());
        FXData data = new FXData(listo);
        var list = new ArrayList<>(data.objects());
        list.replaceAll(object -> {
            if (object.getName().equals("cicle") && object instanceof ParticleEmitter emitter) {
                ParticleEmitter emit = emitter.shallowCopy();
                Constants.LOG.info("{}, {}, {}", ((Constant)emitter.config.getStartSize().x).getNumber(), ((Constant)emitter.config.getStartSize().y).getNumber(), ((Constant)emitter.config.getStartSize().z).getNumber());
//                emit.config.setStartSize(new NumberFunction3(3.6, 3.6, 3.6));
                return emit;
            }

            return object;
        });
        list.forEach(object -> {
            if (object.getName().equals("cicle") && object instanceof ParticleEmitter emitter) {
//                emitter.config.setLooping(true);
//                emitter.config.setDuration(20);
//                emitter.config.setStartSize(new NumberFunction3(3.6, 3.6, 3.6));
//                emitter.config.renderer.setMaterials();
//                Constants.LOG.info("{}", emitter.config.isLooping());
            }

            /*if (object.getName().equals("cicle") && object instanceof ParticleEmitter emitter) {
                Constants.LOG.info("{}, {}, {}", ((Constant)emitter.config.getStartSize().x).getNumber(), ((Constant)emitter.config.getStartSize().y).getNumber(), ((Constant)emitter.config.getStartSize().z).getNumber());
                Constants.LOG.info("2");
            }*/
        });
        FXData fxData = new FXData(list);
//        this.runtime = new FXRuntime(fxData.copy(false));
//        Constants.LOG.info("{}", fxRuntime.fxData.objects().stream().map(IFXObject::getName).toList());
//        Constants.LOG.info("{}", list.stream().map(IFXObject::getName).toList());

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

        var lost = this.runtime.findObject("cicle");
        if (lost instanceof ParticleEmitter emitter) {
            Constants.LOG.info("{}", emitter.config.getDuration());
//            this.runtime.objects.replace(emitter.id());
//            Constants.LOG.info("{}", object);
        }

        this.runtime.emmit(this, delay);
        effects.add(this);
    }
}
