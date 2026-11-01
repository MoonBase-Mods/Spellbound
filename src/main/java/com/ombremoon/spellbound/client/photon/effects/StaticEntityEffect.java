package com.ombremoon.spellbound.client.photon.effects;

import com.lowdragmc.lowdraglib2.client.renderer.impl.IModelRenderer;
import com.lowdragmc.lowdraglib2.configurator.IConfigurable;
import com.lowdragmc.lowdraglib2.math.GradientColor;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXData;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.lowdragmc.photon.client.gameobject.emitter.data.LightOverLifetimeSetting;
import com.lowdragmc.photon.client.gameobject.emitter.data.MaterialSetting;
import com.lowdragmc.photon.client.gameobject.emitter.data.material.TextureMaterial;
import com.lowdragmc.photon.client.gameobject.emitter.data.material.UIResourceMaterial;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.Gradient;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.RandomGradient;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleRendererSetting;
import com.ombremoon.spellbound.client.photon.ConfigurableEffect;
import com.ombremoon.spellbound.client.photon.FXObjectFactory;
import com.ombremoon.spellbound.client.photon.PhotonHelper;
import com.ombremoon.spellbound.client.photon.converter.ConfigBuilder;
import com.ombremoon.spellbound.client.photon.data.NumberFunctionBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.util.Color;

import java.awt.*;
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

    @SuppressWarnings("unchecked")
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

        ConfigurableEffect effect = (ConfigurableEffect) this;
        this.runtime = effect.modifyFXRuntime(fx, level.registryAccess());
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
            var registries = entity.registryAccess();
//            emitter.config.renderer.setRenderMode(ParticleRendererSetting.Mode.Model);
//            emitter.config.renderer.setModel(new IModelRenderer(CommonClass.customLocation("test")));
//            Constants.LOG.info("{}", emitter.config.serializeNBT(entity.registryAccess()));
        }

        this.runtime.emmit(this, delay);
        effects.add(this);
    }

}
